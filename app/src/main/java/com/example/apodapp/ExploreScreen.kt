package com.example.apodapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apodapp.ui.theme.EmptyListComponent

private const val PAGE_COUNT = 50

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(viewModel: MainViewModel) {
    val apodList by viewModel.exploreApods.collectAsStateWithLifecycle()
    val isLoading by viewModel.isExploreLoading.collectAsStateWithLifecycle()
    val errorState by viewModel.errorState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (!errorState.isNullOrEmpty()) {
            AlertDialog(
                text = { Text(text = errorState.orEmpty()) },
                onDismissRequest = {
                    viewModel.clearErrorState()
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.clearErrorState()
                    }) {
                        Text(text = "Dismiss")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearErrorState()
                        viewModel.getApods(
                            count = PAGE_COUNT
                        )
                    }) {
                        Text(text = "Retry")
                    }

                }
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Explore",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(8.dp)
            )
            if (apodList.isEmpty() && !isLoading) {
                EmptyListComponent()
                return
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(-350.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = true
            ) {
                items(apodList) { item ->
                    val dismissState = rememberDismissState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                DismissValue.DismissedToEnd -> {
                                    viewModel.removeFromExplores(item)
                                    false
                                }

                                DismissValue.DismissedToStart -> {
                                    viewModel.removeFromExplores(item)
                                    false
                                }

                                else -> {
                                    false
                                }
                            }
                        },
                    )
                    SwipeToDismiss(
                        modifier = Modifier,
                        directions = setOf(
                            DismissDirection.StartToEnd,
                            DismissDirection.EndToStart
                        ),
                        state = dismissState,
                        background = {

                        }, dismissContent = {
                            val color by animateColorAsState(
                                targetValue = when (dismissState.targetValue) {
                                    DismissValue.Default -> Color.Transparent
                                    DismissValue.DismissedToEnd -> Color.Green.copy(.4f)
                                    DismissValue.DismissedToStart -> Color.Red.copy(.4f)
                                }, label = ""
                            )
                            ApodCardComponent(
                                apodItem = item,
                                showLikeButton = false,
                                showShareButton = false,
                                onFavClicked = {
                                    if (!item.isFavorited)
                                        viewModel.addToFavorites(item)
                                    else
                                        viewModel.removeFromFavorites(item)
                                },
                                paintColor = color
                            )
                        }
                    )
                }
            }
        }
    }
}