package com.example.navigationdemo

import com.example.navigator.api.Route

/**
 * Define all navigation routes for this app.
 */
sealed interface AppRoutes : Route {
    // Fragment destinations
    data object FragmentHome : AppRoutes
    data class FragmentDetails(val orderId: String) : AppRoutes

    // Compose destinations
    data object ComposeHome : AppRoutes
    data class ComposeDetails(val productId: String) : AppRoutes

    // Activity destinations
    data object LegacyActivity : AppRoutes
}
