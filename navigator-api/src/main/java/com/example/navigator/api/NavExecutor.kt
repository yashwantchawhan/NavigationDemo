package com.example.navigator.api

/**
 * Executor that handles navigation for a specific host type.
 *
 * Implement this for each host type (Fragment, Compose, Activity):
 * ```kotlin
 * class MyFragmentExecutor(
 *     private val navController: () -> NavController?
 * ) : NavExecutor {
 *     override fun execute(command: NavCommand): Result<Unit> = runCatching {
 *         val nav = navController() ?: error("NavController not ready")
 *         when (val route = command.route) {
 *             is AppRoutes.Home -> nav.navigate(R.id.home)
 *             is AppRoutes.Details -> nav.navigate(R.id.details, bundleOf("id" to route.id))
 *             else -> error("Unsupported route: $route")
 *         }
 *     }
 * }
 * ```
 */
interface NavExecutor {
    fun execute(command: NavCommand): Result<Unit>
}
