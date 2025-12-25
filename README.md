# Navigator

Navigation library for Android that handles Fragments, Compose, and Activities in one place.

## Modules

```
navigator-api/    → Interfaces only (Navigator, Route, NavExecutor)
navigator-impl/   → Core implementation
navigator-hilt/   → Hilt integration for DI
app/              → Demo app
```

## Setup

### Without Hilt
```kotlin
dependencies {
    implementation(project(":navigator-impl"))
}
```

### With Hilt
```kotlin
dependencies {
    implementation(project(":navigator-hilt"))
}
```

## Quick Start

### 1. Define routes

```kotlin
sealed interface AppRoutes : Route {
    data object Home : AppRoutes
    data class Details(val id: String) : AppRoutes
}
```

### 2. Create registry & executor

```kotlin
class AppRouteRegistry : RouteRegistry {
    override fun spec(route: Route) = when (route) {
        AppRoutes.Home -> DestinationSpec(HostType.FRAGMENT, "Home")
        is AppRoutes.Details -> DestinationSpec(HostType.COMPOSE, "Details")
        else -> error("Unknown")
    }
}

class AppFragmentExecutor(
    private val navController: () -> NavController?
) : NavExecutor {
    override fun execute(cmd: NavCommand) = runCatching {
        val nav = navController() ?: error("Not ready")
        when (cmd.route) {
            AppRoutes.Home -> nav.navigate(R.id.home)
            is BackRoute -> nav.popBackStack()
            else -> error("Unsupported")
        }
    }
}
```

### 3. Build navigator

```kotlin
val navigator = NavigatorBuilder(activity)
    .registry(AppRouteRegistry())
    .fragmentNavController { findNavController() }
    .fragmentExecutor(AppFragmentExecutor { findNavController() })
    .build()
```

### 4. Navigate

```kotlin
navigator.navigate(AppRoutes.Details("123"), "button_click")
navigator.back("back_pressed")
```

## With Hilt (Multi-Module)

### In your :core:navigation module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NavigatorBindingsModule {
    @Provides
    fun provideRegistry(): RouteRegistry = AppRouteRegistry()
}
```

### In your :feature module
```kotlin
@Module
@InstallIn(ActivityComponent::class)
object FeatureNavigatorModule {
    @Provides
    @FragmentExecutor
    fun provideExecutor(controllers: NavigatorControllers): NavExecutor =
        FeatureFragmentExecutor { controllers.fragmentNavController }
}
```

### In your Activity
```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var navigatorFactory: NavigatorFactory
    @Inject lateinit var controllers: NavigatorControllers

    private val navigator by lazy {
        navigatorFactory.create(this, controllers)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controllers.fragmentNavController = findNavController(R.id.nav_host)
    }
}
```

### In your Fragment (any module)
```kotlin
class MyFragment : Fragment() {
    fun onClick() {
        navigator().navigate(AppRoutes.Details("123"), "click")
    }
}
```

## Tests

```bash
./gradlew :navigator-impl:test
```

## Features

- Single `navigate()` call instead of scattered NavControllers
- Dedupe double-taps (600ms window)
- Lifecycle-aware (waits for RESUMED)
- Works across Fragment ↔ Compose ↔ Activity
- Full logging for debugging
- Type-safe routes with sealed interfaces
- Hilt support for multi-module apps

## Logs

```
D/Navigator: QUEUED id=abc123 route=Details src=button_click
D/Navigator: SUCCESS id=abc123
W/Navigator: DROPPED id=def456 reason=dedupe_double_tap
```

## License

MIT
