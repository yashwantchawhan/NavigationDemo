package com.example.navigator.impl

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.navigator.api.HostType
import com.example.navigator.api.NavExecutor
import com.example.navigator.api.NavLogger
import com.example.navigator.api.Navigator
import com.example.navigator.api.RouteRegistry

/**
 * Builder for creating a Navigator instance.
 *
 * Usage:
 * ```kotlin
 * val navigator = NavigatorBuilder(activity)
 *     .registry(MyRouteRegistry())
 *     .fragmentNavController { findNavController(R.id.nav_host) }
 *     .fragmentExecutor(MyFragmentExecutor())
 *     .composeNavController { composeNavController }
 *     .composeExecutor(MyComposeExecutor())
 *     .activityExecutor(MyActivityExecutor())
 *     .onHostSwitch { hostType -> switchVisibility(hostType) }
 *     .build()
 * ```
 */
class NavigatorBuilder(private val activity: FragmentActivity) {

    private var registry: RouteRegistry? = null
    private var logger: NavLogger = DefaultNavLogger()

    private var fragmentNavController: (() -> NavController?)? = null
    private var fragmentExecutor: NavExecutor? = null

    private var composeNavController: (() -> NavHostController?)? = null
    private var composeExecutor: NavExecutor? = null

    private var activityExecutor: NavExecutor? = null

    private var onHostSwitch: (HostType) -> Unit = {}

    /**
     * Set the route registry (required).
     */
    fun registry(registry: RouteRegistry) = apply {
        this.registry = registry
    }

    /**
     * Set a custom logger. Defaults to Logcat logging.
     */
    fun logger(logger: NavLogger) = apply {
        this.logger = logger
    }

    /**
     * Set Fragment NavController provider.
     */
    fun fragmentNavController(provider: () -> NavController?) = apply {
        this.fragmentNavController = provider
    }

    /**
     * Set Fragment navigation executor.
     */
    fun fragmentExecutor(executor: NavExecutor) = apply {
        this.fragmentExecutor = executor
    }

    /**
     * Set Compose NavHostController provider.
     */
    fun composeNavController(provider: () -> NavHostController?) = apply {
        this.composeNavController = provider
    }

    /**
     * Set Compose navigation executor.
     */
    fun composeExecutor(executor: NavExecutor) = apply {
        this.composeExecutor = executor
    }

    /**
     * Set Activity navigation executor.
     */
    fun activityExecutor(executor: NavExecutor) = apply {
        this.activityExecutor = executor
    }

    /**
     * Callback when switching between Fragment and Compose hosts.
     * Use this to toggle visibility of your navigation containers.
     */
    fun onHostSwitch(callback: (HostType) -> Unit) = apply {
        this.onHostSwitch = callback
    }

    /**
     * Build the Navigator instance.
     */
    fun build(): Navigator {
        val registry = this.registry
            ?: error("RouteRegistry is required. Call registry() before build().")

        val executors = mutableMapOf<HostType, NavExecutor>()
        val readyChecks = mutableMapOf<HostType, () -> Boolean>()

        // Fragment setup
        fragmentExecutor?.let { executor ->
            executors[HostType.FRAGMENT] = executor
            readyChecks[HostType.FRAGMENT] = {
                activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
                        fragmentNavController?.invoke() != null
            }
        }

        // Compose setup
        composeExecutor?.let { executor ->
            executors[HostType.COMPOSE] = executor
            readyChecks[HostType.COMPOSE] = {
                activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
                        composeNavController?.invoke() != null
            }
        }

        // Activity setup
        activityExecutor?.let { executor ->
            executors[HostType.ACTIVITY] = executor
            readyChecks[HostType.ACTIVITY] = { true }
        }

        val router = Router(
            registry = registry,
            logger = logger,
            executors = executors,
            hostReadyChecks = readyChecks,
            onHostSwitch = onHostSwitch
        )

        return AppNavigator(
            registry = registry,
            router = router,
            logger = logger
        )
    }
}
