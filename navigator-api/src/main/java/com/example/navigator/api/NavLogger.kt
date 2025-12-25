package com.example.navigator.api

/**
 * Logger for navigation events.
 *
 * Implement this to customize logging (e.g., send to analytics):
 * ```kotlin
 * class AnalyticsNavLogger : NavLogger {
 *     override fun onQueued(command: NavCommand, spec: DestinationSpec) {
 *         analytics.track("nav_queued", mapOf("route" to spec.name))
 *     }
 *     // ... other methods
 * }
 * ```
 */
interface NavLogger {
    fun onQueued(command: NavCommand, spec: DestinationSpec)
    fun onExecuted(command: NavCommand, hostType: HostType)
    fun onSuccess(command: NavCommand)
    fun onFailure(command: NavCommand, error: Throwable)
    fun onDropped(command: NavCommand, reason: String)
}
