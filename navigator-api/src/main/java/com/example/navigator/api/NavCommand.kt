package com.example.navigator.api

import java.util.UUID

/**
 * Represents a navigation command with metadata for logging and debugging.
 */
data class NavCommand(
    val id: String = UUID.randomUUID().toString().take(8),
    val route: Route,
    val source: String,
    val timestampMs: Long = System.currentTimeMillis()
)
