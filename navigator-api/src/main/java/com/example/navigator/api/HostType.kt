package com.example.navigator.api

/**
 * Types of navigation hosts supported.
 */
enum class HostType {
    FRAGMENT,
    COMPOSE,
    ACTIVITY
}

/**
 * Specification for a navigation destination.
 */
data class DestinationSpec(
    val host: HostType,
    val name: String
)
