# Navigator

Navigation library for Android that handles Fragments, Compose, and Activities in one place. No more scattered `findNavController()` calls.

## Why?

- One `navigator.navigate()` call instead of managing NavControllers everywhere
- Built-in double-tap protection
- Logs every navigation for easy debugging
- Works across Fragment ↔ Compose ↔ Activity

## Setup

```kotlin
dependencies {
    implementation(project(":navigator-impl"))
}
```

## Usage

### 1. Define routes

```kotlin
sealed interface AppRoutes : Route {
    data object Home : AppRoutes
    data class Details(val id: String) : AppRoutes
}
```

### 2. Create registry

```kotlin
class AppRouteRegistry : RouteRegistry {
    override fun spec(route: Route) = when (route) {
        AppRoutes.Home -> DestinationSpec(HostType.FRAGMENT, "Home")
        is AppRoutes.Details -> DestinationSpec(HostType.COMPOSE, "Details")
        else -> error("Unknown route")
    }
}
```

### 3. Create executor

```kotlin
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

### 4. Build navigator in Activity

```kotlin
class MainActivity : AppCompatActivity(), NavigatorProvider {

    override val navigator by lazy {
        NavigatorBuilder(this)
            .registry(AppRouteRegistry())
            .fragmentNavController { findNavController() }
            .fragmentExecutor(AppFragmentExecutor { findNavController() })
            .build()
    }
}
```

### 5. Navigate

```kotlin
// From Fragment
navigator.navigate(AppRoutes.Details("123"), "button_click")

// Go back
navigator.back("back_pressed")
```

## Module Structure

```
navigator-api/    → Public interfaces (Navigator, Route, NavExecutor)
navigator-impl/   → Implementation (NavigatorBuilder, Router, logging)
app/              → Demo app
```

## Logs

```
D/Navigator: QUEUED id=abc123 route=Details src=button_click
D/Navigator: SUCCESS id=abc123
W/Navigator: DROPPED id=def456 reason=dedupe_double_tap
```

## Run Demo

```bash
./gradlew installDebug
```

## License

MIT
