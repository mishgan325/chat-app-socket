package ru.mishgan325.chatappsocket.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import ru.mishgan325.chatappsocket.dto.TabBarItem
import ru.mishgan325.chatappsocket.presentation.components.TabView
import ru.mishgan325.chatappsocket.presentation.navigation.NavigationHost

@Composable
fun MainScreen(startDestination: String) {
    val navController = rememberNavController()
    val currentDestinationState = navController.currentBackStackEntryFlow.collectAsState(
        initial = navController.currentBackStackEntry
    )
    val currentRoute = currentDestinationState.value?.destination?.route

    val tabBarItems = TabBarItem.items // если ты сделал sealed class
    val bottomBarRoutes = tabBarItems.map { it.route }

    val showBottomBar = bottomBarRoutes.any { route ->
        currentRoute?.startsWith(route) == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                TabView(tabBarItems, navController)
            }
        }
    ) { innerPadding ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            startDestination = startDestination
        )
    }
}
