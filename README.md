# Navigator - Unified Navigation Library for Android

A clean architecture navigation library that works across **Fragments**, **Jetpack Compose**, and **Activities**. Users only depend on the API module - implementation details are hidden.

## Problem It Solves

- Scattered `findNavController().navigate()` calls everywhere
- Hard to track navigation flow and debug issues
- Double-tap bugs and race conditions
- No central logging/observability
- Inconsistent handling across Fragments and Compose

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│  Your App Code                                           │
│  navigator.navigate(MyRoutes.Details("123"), "click")    │
└──────────────────────────┬──────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│  navigator-api (what you depend on)                      │
│  - Navigator interface                                   │
│  - Route interface                                       │
│  - NavExecutor interface                                 │
│  - RouteRegistry interface                               │
└──────────────────────────┬──────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────┐
│  navigator-impl (hidden from you)                        │
│  - NavigatorBuilder (entry point)                        │
│  - AppNavigator (dedupe, queue, logging)                │
│  - Router (lifecycle-aware dispatch)                     │
└──────────────────────────┬──────────────────────────────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
   Your Executors    (Fragment/Compose/Activity)
```

## Module Structure

```
NavigationDemo/
├── navigator-api/          # Public interfaces (depend on this)
│   └── com.example.navigator.api/
│       ├── Navigator.kt           # Main interface
│       ├── NavigatorProvider.kt   # For Fragment access
│       ├── Route.kt               # Base route interface
│       ├── NavCommand.kt          # Command with metadata
│       ├── NavExecutor.kt         # Executor interface
│       ├── RouteRegistry.kt       # Route → Host mapping
│       ├── HostType.kt            # FRAGMENT, COMPOSE, ACTIVITY
│       └── NavLogger.kt           # Logging interface
│
├── navigator-impl/         # Implementation (hidden)
│   └── com.example.navigator.impl/
│       ├── NavigatorBuilder.kt    # Builder to create Navigator
│       ├── AppNavigator.kt        # Main implementation
│       ├── Router.kt              # Dispatch logic
│       └── DefaultNavLogger.kt    # Logcat logger
│
└── app/                    # Demo app
    └── com.example.navigationdemo/
        ├── AppRoutes.kt           # Your routes
        ├── AppRouteRegistry.kt    # Your registry
        ├── AppFragmentExecutor.kt # Your Fragment executor
        ├── AppComposeExecutor.kt  # Your Compose executor
        ├── AppActivityExecutor.kt # Your Activity executor
        └── MainActivity.kt        # Wiring with NavigatorBuilder
```

## Quick Start

### Step 1: Add Dependency

```kotlin
// build.gradle.kts
dependencies {
    implementation(project(":navigator-impl"))
    // or when published:
    // implementation("com.example:navigator-impl:1.0.0")
}
```

### Step 2: Define Your Routes

```kotlin
// AppRoutes.kt
sealed interface AppRoutes : Route {
    data object Home : AppRoutes
    data class Details(val id: String) : AppRoutes
    data object Settings : AppRoutes
}
```

### Step 3: Create Route Registry

```kotlin
// AppRouteRegistry.kt
class AppRouteRegistry : RouteRegistry {
    override fun spec(route: Route): DestinationSpec = when (route) {
        AppRoutes.Home -> DestinationSpec(HostType.FRAGMENT, "Home")
        is AppRoutes.Details -> DestinationSpec(HostType.COMPOSE, "Details")
        AppRoutes.Settings -> DestinationSpec(HostType.ACTIVITY, "Settings")
        is BackRoute -> DestinationSpec(HostType.FRAGMENT, "Back")
        else -> error("Unknown route: $route")
    }
}
```

### Step 4: Create Executors

```kotlin
// AppFragmentExecutor.kt
class AppFragmentExecutor(
    private val navController: () -> NavController?
) : NavExecutor {
    override fun execute(command: NavCommand): Result<Unit> = runCatching {
        val nav = navController() ?: error("NavController not ready")
        when (val route = command.route) {
            AppRoutes.Home -> nav.navigate(R.id.home)
            is AppRoutes.Details -> nav.navigate(R.id.details, bundleOf("id" to route.id))
            is BackRoute -> nav.popBackStack()
            else -> error("Unsupported: $route")
        }
    }
}

// AppComposeExecutor.kt
class AppComposeExecutor(
    private val navController: () -> NavHostController?
) : NavExecutor {
    override fun execute(command: NavCommand): Result<Unit> = runCatching {
        val nav = navController() ?: error("NavController not ready")
        when (val route = command.route) {
            is AppRoutes.Details -> nav.navigate("details/${route.id}")
            is BackRoute -> nav.popBackStack()
            else -> error("Unsupported: $route")
        }
    }
}
```

### Step 5: Build Navigator in Activity

```kotlin
// MainActivity.kt
class MainActivity : AppCompatActivity(), NavigatorProvider {

    private lateinit var _navigator: Navigator
    override val navigator: Navigator get() = _navigator

    private var composeNavController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Build Navigator using the library
        _navigator = NavigatorBuilder(this)
            .registry(AppRouteRegistry())
            .fragmentNavController { findFragmentNavController() }
            .fragmentExecutor(AppFragmentExecutor { findFragmentNavController() })
            .composeNavController { composeNavController }
            .composeExecutor(AppComposeExecutor { composeNavController })
            .activityExecutor(AppActivityExecutor(this))
            .onHostSwitch { hostType -> switchVisibility(hostType) }
            .build()

        // Set up Compose
        findViewById<ComposeView>(R.id.composeHost).setContent {
            val navController = rememberNavController()
            LaunchedEffect(Unit) { composeNavController = navController }
            MyNavGraph(navController, navigator)
        }
    }

    private fun findFragmentNavController(): NavController? {
        return (supportFragmentManager.findFragmentById(R.id.fragmentNavHost)
            as? NavHostFragment)?.navController
    }

    private fun switchVisibility(host: HostType) {
        when (host) {
            HostType.COMPOSE -> {
                composeView.visibility = View.VISIBLE
                fragmentNavHost.visibility = View.GONE
            }
            HostType.FRAGMENT -> {
                composeView.visibility = View.GONE
                fragmentNavHost.visibility = View.VISIBLE
            }
            HostType.ACTIVITY -> { /* no visibility change */ }
        }
    }
}
```

### Step 6: Navigate!

```kotlin
// From Fragment
class MyFragment : Fragment() {
    private val navigator: Navigator
        get() = (requireActivity() as NavigatorProvider).navigator

    fun onButtonClick() {
        navigator.navigate(AppRoutes.Details("123"), "button_click")
    }
}

// From Compose
@Composable
fun MyScreen(navigator: Navigator) {
    Button(onClick = {
        navigator.navigate(AppRoutes.Details("456"), "compose_click")
    }) {
        Text("Go to Details")
    }
}
```

## Features

| Feature | Description |
|---------|-------------|
| **Clean Architecture** | API/Impl separation - users don't see implementation |
| **Single Entry Point** | Just `navigator.navigate(route, source)` |
| **Double-tap Protection** | 600ms dedupe window prevents duplicates |
| **Lifecycle-aware** | Waits for hosts to be RESUMED before navigating |
| **Cross-host Navigation** | Seamlessly switch between Fragment ↔ Compose |
| **Full Logging** | Every navigation logged with unique ID |
| **Type-safe Routes** | Compile-time safety with sealed interfaces |
| **Extensible** | Custom loggers, executors, registries |

## Logging

All navigation events are logged:

```
D/Navigator: QUEUED id=abc12345 route=Details src=button_click
D/Navigator: EXECUTED id=abc12345 host=COMPOSE route=Details(id=123)
D/Navigator: SUCCESS id=abc12345

W/Navigator: DROPPED id=def67890 reason=dedupe_double_tap route=Details
E/Navigator: FAIL id=ghi11111 route=Settings reason=NavController not ready
```

### Custom Logger

```kotlin
class AnalyticsNavLogger : NavLogger {
    override fun onQueued(command: NavCommand, spec: DestinationSpec) {
        analytics.track("navigation_started", mapOf("destination" to spec.name))
    }
    override fun onSuccess(command: NavCommand) {
        analytics.track("navigation_completed", mapOf("id" to command.id))
    }
    // ... other methods
}

// Use it:
NavigatorBuilder(activity)
    .logger(AnalyticsNavLogger())
    // ...
    .build()
```

## Running the Demo

```bash
# Build
./gradlew assembleDebug

# Install
./gradlew installDebug

# Test deep links
adb shell am start -a android.intent.action.VIEW -d "myapp://order/12345"
```

## API Reference

### Navigator
```kotlin
interface Navigator {
    fun navigate(route: Route, source: String)
    fun back(source: String)
}
```

### Route
```kotlin
interface Route  // Marker interface - extend with sealed interface
data object BackRoute : Route  // Special back navigation
```

### NavExecutor
```kotlin
interface NavExecutor {
    fun execute(command: NavCommand): Result<Unit>
}
```

### RouteRegistry
```kotlin
interface RouteRegistry {
    fun spec(route: Route): DestinationSpec
}
```

### NavigatorBuilder
```kotlin
NavigatorBuilder(activity)
    .registry(registry)                    // Required
    .logger(logger)                        // Optional, defaults to Logcat
    .fragmentNavController { ... }         // Optional
    .fragmentExecutor(executor)            // Optional
    .composeNavController { ... }          // Optional
    .composeExecutor(executor)             // Optional
    .activityExecutor(executor)            // Optional
    .onHostSwitch { hostType -> ... }      // Optional
    .build()
```

## License

MIT License - Feel free to use in your projects.
