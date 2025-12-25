package com.example.navigationdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.navigator.api.Navigator

@Composable
fun ComposeNavGraph(
    navController: NavHostController,
    navigator: Navigator
) {
    NavHost(navController, startDestination = "composeHome") {

        composable("composeHome") {
            ComposeHomeScreen(
                goDetails = { navigator.navigate(AppRoutes.ComposeDetails(it), "composeHome_click") },
                goFragment = { navigator.navigate(AppRoutes.FragmentHome, "composeHome_to_fragment") },
                goLegacy = { navigator.navigate(AppRoutes.LegacyActivity, "composeHome_to_legacy") }
            )
        }

        composable("composeDetails/{id}") { backStack ->
            val id = backStack.arguments?.getString("id").orEmpty()
            ComposeDetailsScreen(id = id, back = { navigator.back("composeDetails_back") })
        }
    }
}

@Composable
fun ComposeHomeScreen(
    goDetails: (String) -> Unit,
    goFragment: () -> Unit,
    goLegacy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Compose Home", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { goDetails("P-42") }) { Text("Compose → Details") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = goFragment) { Text("Compose → Fragment Home") }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = goLegacy) { Text("Compose → Legacy Activity") }
    }
}

@Composable
fun ComposeDetailsScreen(id: String, back: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Compose Details", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Product ID: $id", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = back) { Text("Back") }
    }
}
