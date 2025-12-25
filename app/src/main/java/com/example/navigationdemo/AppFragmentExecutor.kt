package com.example.navigationdemo

import android.os.Bundle
import androidx.navigation.NavController
import com.example.navigator.api.BackRoute
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavExecutor

/**
 * Handles Fragment navigation.
 */
class AppFragmentExecutor(
    private val navController: () -> NavController?
) : NavExecutor {

    override fun execute(command: NavCommand): Result<Unit> = runCatching {
        val nav = navController() ?: error("Fragment NavController not ready")

        when (val route = command.route) {
            AppRoutes.FragmentHome -> nav.navigate(R.id.fragmentHome)

            is AppRoutes.FragmentDetails -> {
                val args = Bundle().apply { putString("orderId", route.orderId) }
                nav.navigate(R.id.fragmentDetails, args)
            }

            is BackRoute -> nav.popBackStack()

            else -> error("Route not supported by Fragment executor: $route")
        }
    }
}
