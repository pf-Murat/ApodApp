package com.example.apodapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val PAGE_COUNT = 50

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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(apodList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize(.9f)
                            .aspectRatio(1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.date,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}