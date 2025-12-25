package com.example.navigator.hilt

import android.app.Activity
import androidx.fragment.app.Fragment
import com.example.navigator.api.Navigator
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Entry point for accessing Navigator from non-Hilt classes.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavigatorEntryPoint {
    fun navigator(): Navigator
}

/**
 * Extension to get Navigator from Activity.
 */
fun Activity.navigator(): Navigator {
    return EntryPointAccessors
        .fromApplication(applicationContext, NavigatorEntryPoint::class.java)
        .navigator()
}

/**
 * Extension to get Navigator from Fragment.
 */
fun Fragment.navigator(): Navigator {
    return EntryPointAccessors
        .fromApplication(requireContext().applicationContext, NavigatorEntryPoint::class.java)
        .navigator()
}
