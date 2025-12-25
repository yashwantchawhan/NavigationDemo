package com.example.navigator.api

/**
 * Base interface for all navigation routes.
 *
 * Define your routes as a sealed interface extending this:
 * ```kotlin
 * sealed interface AppRoutes : Route {
 *     data object Home : AppRoutes
 *     data class Details(val id: String) : AppRoutes
 *     data object Settings : AppRoutes
 * }
 * ```
 */
interface Route

/**
 * Special route for back navigation.
 * Used internally by Navigator.back()
 */
data object BackRoute : Route
