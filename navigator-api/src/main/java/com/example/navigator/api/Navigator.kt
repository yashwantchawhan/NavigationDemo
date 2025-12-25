package com.example.navigator.api

/**
 * Main entry point for navigation.
 *
 * Usage:
 * ```kotlin
 * navigator.navigate(MyRoutes.Details("123"), "button_click")
 * navigator.back("back_pressed")
 * ```
 */
interface Navigator {
    /**
     * Navigate to a destination.
     *
     * @param route The destination route
     * @param source Identifier for logging/debugging (e.g., "home_button_click")
     */
    fun navigate(route: Route, source: String)

    /**
     * Navigate back in the current navigation stack.
     *
     * @param source Identifier for logging/debugging
     */
    fun back(source: String)
}

/**
 * Implement this interface in your Activity to provide Navigator access to Fragments.
 */
interface NavigatorProvider {
    val navigator: Navigator
}
