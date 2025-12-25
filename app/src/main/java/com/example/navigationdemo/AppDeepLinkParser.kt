package com.example.navigationdemo

import android.content.Intent
import com.example.navigator.api.Route

/**
 * Parses deep links into app routes.
 */
class AppDeepLinkParser {

    fun parse(intent: Intent?): Route? {
        val uri = intent?.data ?: return null

        // myapp://order/123
        if (uri.scheme == "myapp") {
            val segments = uri.pathSegments
            return when (segments.firstOrNull()) {
                "order" -> segments.getOrNull(1)?.let { AppRoutes.FragmentDetails(it) }
                "compose" -> segments.getOrNull(1)?.let { AppRoutes.ComposeDetails(it) }
                "legacy" -> AppRoutes.LegacyActivity
                else -> null
            }
        }

        // https://example.com/order/123
        if (uri.scheme == "https" && uri.host == "example.com") {
            val segments = uri.pathSegments
            return when (segments.firstOrNull()) {
                "order" -> segments.getOrNull(1)?.let { AppRoutes.FragmentDetails(it) }
                else -> null
            }
        }

        return null
    }
}
