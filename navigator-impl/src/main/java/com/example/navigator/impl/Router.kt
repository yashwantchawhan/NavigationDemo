package com.example.navigator.impl

import com.example.navigator.api.BackRoute
import com.example.navigator.api.HostType
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavExecutor
import com.example.navigator.api.NavLogger
import com.example.navigator.api.RouteRegistry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Routes navigation commands to the appropriate executor.
 */
internal class Router(
    private val registry: RouteRegistry,
    private val logger: NavLogger,
    private val executors: Map<HostType, NavExecutor>,
    private val hostReadyChecks: Map<HostType, () -> Boolean> = emptyMap(),
    private val onHostSwitch: (HostType) -> Unit = {},
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    @Volatile
    private var activeHost: HostType = HostType.FRAGMENT

    suspend fun dispatch(cmd: NavCommand) {
        // Special handling for Back - use active host
        if (cmd.route is BackRoute) {
            dispatchBack(cmd)
            return
        }

        val spec = registry.spec(cmd.route)

        // Wait for host to be ready
        val ready = waitUntilReady(spec.host)
        if (!ready) {
            logger.onDropped(cmd, "host_not_ready_${spec.host}")
            return
        }

        // Switch host visibility if needed
        if (spec.host != HostType.ACTIVITY && spec.host != activeHost) {
            activeHost = spec.host
            withContext(mainDispatcher) {
                onHostSwitch(spec.host)
            }
        }

        logger.onExecuted(cmd, spec.host)

        val executor = executors[spec.host]
            ?: error("No executor registered for ${spec.host}")

        val result = executor.execute(cmd)
        result.fold(
            onSuccess = { logger.onSuccess(cmd) },
            onFailure = { logger.onFailure(cmd, it) }
        )
    }

    private suspend fun dispatchBack(cmd: NavCommand) {
        logger.onExecuted(cmd, activeHost)

        val executor = executors[activeHost]
        if (executor == null) {
            logger.onFailure(cmd, IllegalStateException("No executor for $activeHost"))
            return
        }

        val result = executor.execute(cmd)
        result.fold(
            onSuccess = { logger.onSuccess(cmd) },
            onFailure = { logger.onFailure(cmd, it) }
        )
    }

    private suspend fun waitUntilReady(host: HostType): Boolean {
        val check = hostReadyChecks[host] ?: return true

        repeat(10) { // ~1 second max
            if (check()) return true
            delay(100)
        }
        return false
    }
}
