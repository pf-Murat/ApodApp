package com.example.apodapp.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.apodapp.ApodCardComponent
import com.example.apodapp.MainViewModel
import com.example.apodapp.calendarEnd
import com.example.apodapp.calendarStart
import com.example.apodapp.getBackEndTime
import java.util.Calendar



@Composable
fun SearchScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val apodList by viewModel.searchApods.collectAsStateWithLifecycle()
    val errorState by viewModel.errorState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isSearchLoading.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        var startDateForBackend by remember { mutableStateOf(calendarStart.getBackEndTime()) }
        var endDateForBackend by remember { mutableStateOf(calendarEnd.getBackEndTime()) }

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
                            startDate = startDateForBackend,
                            endDate = endDateForBackend
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
                text = "Search",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(8.dp)
            )
            val datePickerStart = DatePickerDialog(
                context,
                { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                    startDateForBackend = "$selectedYear-$selectedMonth-$selectedDayOfMonth"
                    viewModel.getApods(
                        startDate = startDateForBackend,
                        endDate = endDateForBackend
                    )
                },
                calendarStart[Calendar.YEAR],
                calendarStart[Calendar.MONTH],
                calendarStart[Calendar.DAY_OF_MONTH]
            )

            val datePickerEnd = DatePickerDialog(
                context,
                { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
                    endDateForBackend = "$selectedYear-$selectedMonth-$selectedDayOfMonth"
                    viewModel.getApods(
                        startDate = startDateForBackend,
                        endDate = endDateForBackend
                    )
                },
                calendarEnd[Calendar.YEAR],
                calendarEnd[Calendar.MONTH],
                calendarEnd[Calendar.DAY_OF_MONTH]
            )

            Row {
                Button(
                    onClick = { datePickerStart.show() }
                ) {
                    Text(text = startDateForBackend)
                }
                Button(
                    onClick = { datePickerEnd.show() }
                ) {
                    Text(text = endDateForBackend)
                }
            }
            if (apodList.isEmpty() && !isLoading) {
                EmptyListComponent()
                return
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(apodList) { item ->
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
