package com.example.navigator.hilt

import javax.inject.Qualifier

/**
 * Qualifier for Fragment navigation executor.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FragmentExecutor

/**
 * Qualifier for Compose navigation executor.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComposeExecutor

/**
 * Qualifier for Activity navigation executor.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ActivityExecutor
