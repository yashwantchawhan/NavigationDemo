package com.example.navigator.impl

import android.util.Log
import com.example.navigator.api.DestinationSpec
import com.example.navigator.api.HostType
import com.example.navigator.api.NavCommand
import com.example.navigator.api.NavLogger

/**
 * Default implementation of NavLogger that logs to Android Logcat.
 */
internal class DefaultNavLogger : NavLogger {

    override fun onQueued(command: NavCommand, spec: DestinationSpec) {
        Log.d(TAG, "QUEUED id=${command.id} route=${spec.name} src=${command.source}")
    }

    override fun onExecuted(command: NavCommand, hostType: HostType) {
        Log.d(TAG, "EXECUTED id=${command.id} host=${hostType.name} route=${command.route}")
    }

    override fun onSuccess(command: NavCommand) {
        Log.d(TAG, "SUCCESS id=${command.id}")
    }

    override fun onFailure(command: NavCommand, error: Throwable) {
        Log.e(TAG, "FAIL id=${command.id} route=${command.route} reason=${error.message}", error)
    }

    override fun onDropped(command: NavCommand, reason: String) {
        Log.w(TAG, "DROPPED id=${command.id} reason=$reason route=${command.route}")
    }

    companion object {
        private const val TAG = "Navigator"
    }
}
