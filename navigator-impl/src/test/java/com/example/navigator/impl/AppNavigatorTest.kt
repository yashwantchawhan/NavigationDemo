package com.example.navigator.impl

import com.example.navigator.api.BackRoute
import com.example.navigator.api.DestinationSpec
import com.example.navigator.api.HostType
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavExecutor
import com.example.navigator.api.NavLogger
import com.example.navigator.api.Route
import com.example.navigator.api.RouteRegistry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppNavigatorTest {

    // Test routes
    sealed interface TestRoutes : Route {
        data object Home : TestRoutes
        data class Details(val id: String) : TestRoutes
    }

    private lateinit var registry: RouteRegistry
    private lateinit var router: Router
    private lateinit var logger: NavLogger
    private lateinit var navigator: AppNavigator
    private val testDispatcher = UnconfinedTestDispatcher()
    private var currentTime = 0L

    @Before
    fun setup() {
        currentTime = 0L

        registry = mockk {
            every { spec(TestRoutes.Home) } returns DestinationSpec(HostType.FRAGMENT, "Home")
            every { spec(match<Route> { it is TestRoutes.Details }) } returns DestinationSpec(HostType.FRAGMENT, "Details")
            every { spec(BackRoute) } returns DestinationSpec(HostType.FRAGMENT, "Back")
        }

        router = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        navigator = AppNavigator(
            registry = registry,
            router = router,
            logger = logger,
            dedupeWindowMs = 100,
            dispatcher = testDispatcher,
            timeProvider = { currentTime }
        )
    }

    @Test
    fun `navigate calls router dispatch`() = runTest {
        navigator.navigate(TestRoutes.Home, "test_source")
        advanceUntilIdle()

        val slot = slot<NavCommand>()
        coVerify { router.dispatch(capture(slot)) }

        assertEquals(TestRoutes.Home, slot.captured.route)
        assertEquals("test_source", slot.captured.source)
    }

    @Test
    fun `navigate logs queued event`() = runTest {
        navigator.navigate(TestRoutes.Home, "test")

        verify {
            logger.onQueued(
                match { it.route == TestRoutes.Home },
                match { it.name == "Home" }
            )
        }
    }

    @Test
    fun `back navigates with BackRoute`() = runTest {
        navigator.back("back_source")
        advanceUntilIdle()

        val slot = slot<NavCommand>()
        coVerify { router.dispatch(capture(slot)) }

        assertEquals(BackRoute, slot.captured.route)
        assertEquals("back_source", slot.captured.source)
    }

    @Test
    fun `duplicate navigations within dedupe window are dropped`() = runTest {
        navigator.navigate(TestRoutes.Home, "source1")
        navigator.navigate(TestRoutes.Home, "source1") // Same route + source
        advanceUntilIdle()

        // Only first should go through
        coVerify(exactly = 1) { router.dispatch(any()) }
        verify { logger.onDropped(any(), "dedupe_double_tap") }
    }

    @Test
    fun `different routes are not deduped`() = runTest {
        navigator.navigate(TestRoutes.Home, "source")
        navigator.navigate(TestRoutes.Details("123"), "source")
        advanceUntilIdle()

        coVerify(exactly = 2) { router.dispatch(any()) }
    }

    @Test
    fun `same route after dedupe window goes through`() = runTest {
        navigator.navigate(TestRoutes.Home, "source")
        currentTime = 150 // Advance time beyond dedupe window (100ms)
        navigator.navigate(TestRoutes.Home, "source")
        advanceUntilIdle()

        coVerify(exactly = 2) { router.dispatch(any()) }
    }

    @Test
    fun `nav command has unique id`() = runTest {
        val commands = mutableListOf<NavCommand>()

        coEvery { router.dispatch(capture(commands)) } returns Unit

        navigator.navigate(TestRoutes.Home, "s1")
        navigator.navigate(TestRoutes.Details("1"), "s2")
        advanceUntilIdle()

        assertEquals(2, commands.size)
        assertTrue(commands[0].id != commands[1].id)
    }
}
