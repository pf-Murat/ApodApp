package com.example.apodapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apodapp.ApodCardComponent
import com.example.apodapp.MainViewModel

@Composable
fun FavoritesScreen(viewModel: MainViewModel) {
    val savedApods by viewModel.savedApods.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Favorites",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(8.dp)
            )
            if (savedApods.isEmpty()) {
                EmptyListComponent()
                return
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(savedApods.toList()) { item ->
                    ApodCardComponent(
                        apodItem = item,
                        onFavClicked = {
                            if (!item.isFavorited)
                                viewModel.addToFavorites(item)
                            else
                                viewModel.removeFromFavorites(item)
                        }
                    )
                }
            }
        }
    }
}