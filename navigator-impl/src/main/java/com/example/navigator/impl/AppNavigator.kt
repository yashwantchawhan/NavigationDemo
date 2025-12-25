package com.example.navigator.impl

import com.example.navigator.api.BackRoute
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavLogger
import com.example.navigator.api.Navigator
import com.example.navigator.api.Route
import com.example.navigator.api.RouteRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Main implementation of Navigator.
 * Handles queuing, deduplication, and dispatching of navigation commands.
 */
internal class AppNavigator(
    private val registry: RouteRegistry,
    private val router: Router,
    private val logger: NavLogger,
    private val dedupeWindowMs: Long = 600L
) : Navigator {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private val channel = Channel<NavCommand>(capacity = Channel.BUFFERED)

    // Simple dedupe tracking
    private var lastKey: String? = null
    private var lastTs: Long = 0

    init {
        scope.launch {
            for (cmd in channel) {
                router.dispatch(cmd)
            }
        }
    }

    override fun navigate(route: Route, source: String) {
        val cmd = NavCommand(route = route, source = source)
        val key = "${route::class.java.simpleName}#$source"
        val now = System.currentTimeMillis()

        // Dedupe rapid double-taps
        if (key == lastKey && (now - lastTs) < dedupeWindowMs) {
            logger.onDropped(cmd, "dedupe_double_tap")
            return
        }
        lastKey = key
        lastTs = now

        val spec = registry.spec(route)
        logger.onQueued(cmd, spec)
        channel.trySend(cmd)
    }

    override fun back(source: String) {
        navigate(BackRoute, source)
    }
}
