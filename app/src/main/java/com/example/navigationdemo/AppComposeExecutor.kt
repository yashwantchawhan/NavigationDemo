package com.example.navigationdemo

import androidx.navigation.NavHostController
import com.example.navigator.api.BackRoute
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavExecutor

/**
 * Handles Compose navigation.
 */
class AppComposeExecutor(
    private val navController: () -> NavHostController?
) : NavExecutor {

    override fun execute(command: NavCommand): Result<Unit> = runCatching {
        val nav = navController() ?: error("Compose NavHostController not ready")

        when (val route = command.route) {
            AppRoutes.ComposeHome -> nav.navigate("composeHome")

            is AppRoutes.ComposeDetails -> nav.navigate("composeDetails/${route.productId}")

            is BackRoute -> nav.popBackStack()

            else -> error("Route not supported by Compose executor: $route")
        }
    }
}
