package com.example.navigationdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.navigator.api.HostType
import com.example.navigator.api.Navigator
import com.example.navigator.api.NavigatorProvider
import com.example.navigator.impl.NavigatorBuilder
import com.example.navigationdemo.ui.theme.NavigationDemoTheme

class MainActivity : AppCompatActivity(), NavigatorProvider {

    private lateinit var _navigator: Navigator
    override val navigator: Navigator get() = _navigator

    private val deepLinkParser = AppDeepLinkParser()

    // NavController references
    private var composeNavController: NavHostController? = null

    private lateinit var composeView: ComposeView
    private lateinit var fragmentNavHost: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        composeView = findViewById(R.id.composeHost)
        fragmentNavHost = findViewById(R.id.fragmentNavHost)

        // Build Navigator using the library
        _navigator = NavigatorBuilder(this)
            .registry(AppRouteRegistry())
            .fragmentNavController { findFragmentNavController() }
            .fragmentExecutor(AppFragmentExecutor { findFragmentNavController() })
            .composeNavController { composeNavController }
            .composeExecutor(AppComposeExecutor { composeNavController })
            .activityExecutor(AppActivityExecutor(this))
            .onHostSwitch { hostType -> switchHost(hostType) }
            .build()

        // Set up Compose nav host
        composeView.setContent {
            NavigationDemoTheme {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    composeNavController = navController
                }
                ComposeNavGraph(navController = navController, navigator = _navigator)
            }
        }

        // Handle deep link on cold start
        handleDeepLink(intent)
    }

    private fun findFragmentNavController(): NavController? {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentNavHost) as? NavHostFragment
        return navHostFragment?.navController
    }

    private fun switchHost(host: HostType) {
        when (host) {
            HostType.COMPOSE -> {
                composeView.visibility = View.VISIBLE
                fragmentNavHost.visibility = View.GONE
            }
            HostType.FRAGMENT -> {
                composeView.visibility = View.GONE
                fragmentNavHost.visibility = View.VISIBLE
            }
            HostType.ACTIVITY -> {
                // Activity navigation doesn't affect visibility
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val route = deepLinkParser.parse(intent) ?: return
        _navigator.navigate(route, source = "deeplink")
    }
}
