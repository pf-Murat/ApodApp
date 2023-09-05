package com.example.apodapp.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.apodapp.MainViewModel

@Composable
fun MainNavigationHolderScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = NavBarScreens.Search.route) {
        composable(NavBarScreens.Search.route) {
            SearchScreen(viewModel = viewModel)
        }
        composable(NavBarScreens.Explore.route) {
            ExploreScreen(viewModel = viewModel)
        }
        composable(NavBarScreens.Favorites.route) {
            FavoritesScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun MainScreenBottomNav(
    navController: NavHostController,
    bottomBarItems: List<NavBarScreens>
) {
    NavigationBar {
        bottomBarItems.forEach { item ->
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(text = stringResource(id = item.titleRes)) }
            )
        }
    }
}