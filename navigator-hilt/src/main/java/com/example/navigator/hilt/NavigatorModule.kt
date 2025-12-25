package com.example.navigator.hilt

import com.example.navigator.api.NavExecutor
import com.example.navigator.api.NavLogger
import com.example.navigator.api.Navigator
import com.example.navigator.api.RouteRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides Navigator.
 *
 * Users need to provide:
 * - RouteRegistry
 * - NavExecutor bindings with @FragmentExecutor, @ComposeExecutor, @ActivityExecutor qualifiers
 *
 * Optional:
 * - NavLogger (defaults to Logcat)
 */
@Module
@InstallIn(SingletonComponent::class)
object NavigatorModule {

    @Provides
    @Singleton
    fun provideNavigatorConfig(): NavigatorConfig {
        return NavigatorConfig()
    }
}

/**
 * Configuration for Navigator.
 * Extend this in your app module if needed.
 */
data class NavigatorConfig(
    val dedupeWindowMs: Long = 600L,
    val enableLogging: Boolean = true
)
