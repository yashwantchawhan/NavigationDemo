package com.example.navigationdemo

import android.app.Activity
import android.content.Intent
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavExecutor

/**
 * Handles Activity navigation.
 */
class AppActivityExecutor(
    private val activity: Activity
) : NavExecutor {

    override fun execute(command: NavCommand): Result<Unit> = runCatching {
        when (command.route) {
            AppRoutes.LegacyActivity -> {
                activity.startActivity(Intent(activity, LegacyActivity::class.java))
            }

            else -> error("Route not supported by Activity executor: ${command.route}")
        }
    }
}
