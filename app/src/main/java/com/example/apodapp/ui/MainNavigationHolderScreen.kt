package com.example.apodapp.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.apodapp.MainViewModel
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun MainNavigationHolderScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = NavBarScreens.Search.route) {
        composable(NavBarScreens.Search.route) {
            SearchScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(NavBarScreens.Explore.route) {
            ExploreScreen(viewModel = viewModel)
        }
        composable(NavBarScreens.Favorites.route) {
            FavoritesScreen(viewModel = viewModel)
        }
        composable(
            route = "route_full_screen_image/{imageUrl}/{hdImageUrl}",
            arguments = listOf(
                navArgument("imageUrl") {
                    type = NavType.StringType
                },
                navArgument("hdImageUrl") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            FullScreenImageComponent(
                imageUrl = backStackEntry.arguments?.getString("imageUrl").orEmpty().decode(),
                hdImageUrl = backStackEntry.arguments?.getString("hdImageUrl").orEmpty().decode(),
                onCloseClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}

fun createFullScreenImageRoute(
    url: String,
    hdUrl: String
): String {
    val route = "route_full_screen_image/{imageUrl}/{hdImageUrl}"
    return route.replace("{imageUrl}", url.encode()).replace("{hdImageUrl}", hdUrl.encode())
}

private fun String.decode() = URLDecoder.decode(this, "utf-8")
private fun String.encode() = URLEncoder.encode(this, "utf-8")

@Composable
fun MainScreenBottomNav(
    navController: NavHostController,
    bottomBarItems: List<NavBarScreens>
) {
    NavigationBar {
        var selectedItem: NavBarScreens? by remember {
            mutableStateOf(NavBarScreens.Search)
        }
        bottomBarItems.forEach { item ->
            NavigationBarItem(
                selected = selectedItem == item,
                onClick = {
                    if (selectedItem == item) return@NavigationBarItem
                    navController.navigate(item.route)
                    selectedItem = item
                },
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = { Text(text = stringResource(id = item.titleRes)) }
            )
        }
    }
}