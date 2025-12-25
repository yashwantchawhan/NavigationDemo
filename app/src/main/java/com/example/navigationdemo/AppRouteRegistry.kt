package com.example.navigationdemo

import com.example.navigator.api.BackRoute
import com.example.navigator.api.DestinationSpec
import com.example.navigator.api.HostType
import com.example.navigator.api.Route
import com.example.navigator.api.RouteRegistry

/**
 * Maps routes to their host types.
 */
class AppRouteRegistry : RouteRegistry {

    override fun spec(route: Route): DestinationSpec = when (route) {
        // Fragment destinations
        AppRoutes.FragmentHome -> DestinationSpec(HostType.FRAGMENT, "FragmentHome")
        is AppRoutes.FragmentDetails -> DestinationSpec(HostType.FRAGMENT, "FragmentDetails")

        // Compose destinations
        AppRoutes.ComposeHome -> DestinationSpec(HostType.COMPOSE, "ComposeHome")
        is AppRoutes.ComposeDetails -> DestinationSpec(HostType.COMPOSE, "ComposeDetails")

        // Activity destinations
        AppRoutes.LegacyActivity -> DestinationSpec(HostType.ACTIVITY, "LegacyActivity")

        // Back is handled specially by Router
        is BackRoute -> DestinationSpec(HostType.FRAGMENT, "Back")

        else -> error("Unknown route: $route")
    }
}
