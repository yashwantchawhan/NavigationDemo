package com.example.navigator.api

/**
 * Registry that maps routes to their destination specifications.
 *
 * Implement this to define where each route should be handled:
 * ```kotlin
 * class MyRouteRegistry : RouteRegistry {
 *     override fun spec(route: Route): DestinationSpec = when (route) {
 *         is AppRoutes.Home -> DestinationSpec(HostType.FRAGMENT, "Home")
 *         is AppRoutes.Details -> DestinationSpec(HostType.COMPOSE, "Details")
 *         else -> error("Unknown route: $route")
 *     }
 * }
 * ```
 */
interface RouteRegistry {
    fun spec(route: Route): DestinationSpec
}
