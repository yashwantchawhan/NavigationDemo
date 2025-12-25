package com.example.navigator.hilt

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.navigator.api.HostType
import com.example.navigator.api.NavExecutor
import com.example.navigator.api.NavLogger
import com.example.navigator.api.Navigator
import com.example.navigator.api.RouteRegistry
import com.example.navigator.impl.NavigatorBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

/**
 * Activity-scoped Navigator provider.
 *
 * In your app, create a module like:
 * ```
 * @Module
 * @InstallIn(ActivityComponent::class)
 * object AppNavigatorModule {
 *     @Provides
 *     fun provideRouteRegistry(): RouteRegistry = AppRouteRegistry()
 *
 *     @Provides
 *     @FragmentExecutor
 *     fun provideFragmentExecutor(providers: NavigatorControllers): NavExecutor =
 *         AppFragmentExecutor { providers.fragmentNavController }
 * }
 * ```
 */
@Module
@InstallIn(ActivityComponent::class)
object ActivityNavigatorModule {

    @Provides
    @ActivityScoped
    fun provideNavigatorControllers(): NavigatorControllers = NavigatorControllers()
}

/**
 * Holds NavController references.
 * Set these in your Activity's onCreate.
 */
class NavigatorControllers {
    var fragmentNavController: NavController? = null
    var composeNavController: NavHostController? = null
    var onHostSwitch: ((HostType) -> Unit)? = null
}
