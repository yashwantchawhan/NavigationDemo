package com.example.navigator.hilt

import androidx.fragment.app.FragmentActivity
import com.example.navigator.api.NavExecutor
import com.example.navigator.api.NavLogger
import com.example.navigator.api.Navigator
import com.example.navigator.api.RouteRegistry
import com.example.navigator.impl.NavigatorBuilder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Factory to create Navigator with Hilt-injected dependencies.
 *
 * Usage in Activity:
 * ```
 * @AndroidEntryPoint
 * class MainActivity : AppCompatActivity() {
 *     @Inject lateinit var navigatorFactory: NavigatorFactory
 *     @Inject lateinit var controllers: NavigatorControllers
 *
 *     private val navigator by lazy {
 *         navigatorFactory.create(this, controllers)
 *     }
 * }
 * ```
 */
class NavigatorFactory @Inject constructor(
    private val registry: RouteRegistry,
    private val logger: NavLogger?,
    @FragmentExecutor private val fragmentExecutor: NavExecutor?,
    @ComposeExecutor private val composeExecutor: NavExecutor?,
    @ActivityExecutor private val activityExecutor: NavExecutor?
) {
    fun create(
        activity: FragmentActivity,
        controllers: NavigatorControllers
    ): Navigator {
        val builder = NavigatorBuilder(activity).registry(registry)

        logger?.let { builder.logger(it) }

        fragmentExecutor?.let {
            builder.fragmentNavController { controllers.fragmentNavController }
            builder.fragmentExecutor(it)
        }

        composeExecutor?.let {
            builder.composeNavController { controllers.composeNavController }
            builder.composeExecutor(it)
        }

        activityExecutor?.let {
            builder.activityExecutor(it)
        }

        controllers.onHostSwitch?.let {
            builder.onHostSwitch(it)
        }

        return builder.build()
    }
}
