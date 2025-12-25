package com.example.navigator.impl

import com.example.navigator.api.BackRoute
import com.example.navigator.api.DestinationSpec
import com.example.navigator.api.HostType
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavExecutor
import com.example.navigator.api.NavLogger
import com.example.navigator.api.Route
import com.example.navigator.api.RouteRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RouterTest {

    sealed interface TestRoutes : Route {
        data object FragmentScreen : TestRoutes
        data object ComposeScreen : TestRoutes
        data object ActivityScreen : TestRoutes
    }

    private lateinit var registry: RouteRegistry
    private lateinit var logger: NavLogger
    private lateinit var fragmentExecutor: NavExecutor
    private lateinit var composeExecutor: NavExecutor
    private lateinit var activityExecutor: NavExecutor
    private lateinit var router: Router

    private var hostSwitchCalls = mutableListOf<HostType>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        registry = mockk {
            every { spec(TestRoutes.FragmentScreen) } returns
                DestinationSpec(HostType.FRAGMENT, "Fragment")
            every { spec(TestRoutes.ComposeScreen) } returns
                DestinationSpec(HostType.COMPOSE, "Compose")
            every { spec(TestRoutes.ActivityScreen) } returns
                DestinationSpec(HostType.ACTIVITY, "Activity")
            every { spec(BackRoute) } returns
                DestinationSpec(HostType.FRAGMENT, "Back")
        }

        logger = mockk(relaxed = true)

        fragmentExecutor = mockk {
            every { execute(any()) } returns Result.success(Unit)
        }
        composeExecutor = mockk {
            every { execute(any()) } returns Result.success(Unit)
        }
        activityExecutor = mockk {
            every { execute(any()) } returns Result.success(Unit)
        }

        hostSwitchCalls.clear()

        router = Router(
            registry = registry,
            logger = logger,
            executors = mapOf(
                HostType.FRAGMENT to fragmentExecutor,
                HostType.COMPOSE to composeExecutor,
                HostType.ACTIVITY to activityExecutor
            ),
            hostReadyChecks = mapOf(
                HostType.FRAGMENT to { true },
                HostType.COMPOSE to { true },
                HostType.ACTIVITY to { true }
            ),
            onHostSwitch = { hostSwitchCalls.add(it) },
            mainDispatcher = testDispatcher
        )
    }

    @Test
    fun `dispatch routes to fragment executor`() = runTest {
        val cmd = NavCommand(route = TestRoutes.FragmentScreen, source = "test")
        router.dispatch(cmd)

        verify { fragmentExecutor.execute(cmd) }
        verify(exactly = 0) { composeExecutor.execute(any()) }
    }

    @Test
    fun `dispatch routes to compose executor`() = runTest {
        val cmd = NavCommand(route = TestRoutes.ComposeScreen, source = "test")
        router.dispatch(cmd)

        verify { composeExecutor.execute(cmd) }
    }

    @Test
    fun `dispatch routes to activity executor`() = runTest {
        val cmd = NavCommand(route = TestRoutes.ActivityScreen, source = "test")
        router.dispatch(cmd)

        verify { activityExecutor.execute(cmd) }
    }

    @Test
    fun `back uses active host executor`() = runTest {
        // First navigate to compose to set it as active
        router.dispatch(NavCommand(route = TestRoutes.ComposeScreen, source = "test"))

        // Now back should use compose executor
        val backCmd = NavCommand(route = BackRoute, source = "back")
        router.dispatch(backCmd)

        verify { composeExecutor.execute(backCmd) }
    }

    @Test
    fun `logs success on successful navigation`() = runTest {
        val cmd = NavCommand(route = TestRoutes.FragmentScreen, source = "test")
        router.dispatch(cmd)

        verify { logger.onSuccess(cmd) }
    }

    @Test
    fun `logs failure on failed navigation`() = runTest {
        val error = RuntimeException("Nav failed")
        every { fragmentExecutor.execute(any()) } returns Result.failure(error)

        val cmd = NavCommand(route = TestRoutes.FragmentScreen, source = "test")
        router.dispatch(cmd)

        verify { logger.onFailure(cmd, error) }
    }

    @Test
    fun `calls onHostSwitch when switching between fragment and compose`() = runTest {
        router.dispatch(NavCommand(route = TestRoutes.ComposeScreen, source = "test"))

        assert(hostSwitchCalls.contains(HostType.COMPOSE))
    }

    @Test
    fun `does not call onHostSwitch for activity navigation`() = runTest {
        router.dispatch(NavCommand(route = TestRoutes.ActivityScreen, source = "test"))

        assert(!hostSwitchCalls.contains(HostType.ACTIVITY))
    }
}
