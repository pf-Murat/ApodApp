package com.example.apodapp

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavBarScreens(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    object Search : NavBarScreens(
        route = "route_search",
        titleRes = R.string.nav_item_search,
        icon = Icons.Default.Search
    )

    object Explore : NavBarScreens(
        route = "route_explore",
        titleRes = R.string.nav_item_explore,
        icon = Icons.Default.Place
    )

    object Favorites : NavBarScreens(
        route = "route_favorites",
        titleRes = R.string.nav_item_favorites,
        icon = Icons.Default.Favorite
    )
}