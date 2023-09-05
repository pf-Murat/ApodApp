package com.example.apodapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apodapp.ui.theme.ApodApi
import com.example.apodapp.ui.theme.ApodItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL =  "https://api.nasa.gov/planetary/"

class MainViewModel : ViewModel() {

    private val apodApi by lazy { getApodApi() }
    var apods: MutableStateFlow<List<ApodItem>> = MutableStateFlow(emptyList())
        private set
    var errorState: MutableStateFlow<String?> = MutableStateFlow(null)
        private set

    fun clearErrorState() {
        errorState.update { null }
    }

    fun getApods(
        startDate: String? = null,
        endDate: String? = null,
        count: Int? = null
    ) {
        viewModelScope.launch {
            try {
                val response = apodApi.getApods(
                    startDate = startDate,
                    endDate = endDate,
                    count = count
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        apods.update { apodList -> apodList + response.body().orEmpty() }
                    } else {
                        errorState.update { "Empty List Fetched" }
                    }
                }
            } catch (ex: Throwable) {
                errorState.update { ex.cause?.message ?: "ERROR WITH EMPTY MESSAGE" }
            }
        }
    }
}

fun getApodApi() = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApodApi::class.java)