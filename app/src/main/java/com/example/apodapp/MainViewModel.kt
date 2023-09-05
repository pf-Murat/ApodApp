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

private const val BASE_URL = "https://api.nasa.gov/planetary/"
private const val EXPLORE_PAGE_SIZE = 50

class MainViewModel : ViewModel() {

    private val apodApi by lazy { getApodApi() }
    var searchApods: MutableStateFlow<List<ApodItem>> = MutableStateFlow(emptyList())
        private set
    var exploreApods: MutableStateFlow<List<ApodItem>> = MutableStateFlow(emptyList())
        private set
    var savedApods: MutableStateFlow<Set<ApodItem>> = MutableStateFlow(emptySet())
        private set

    var errorState: MutableStateFlow<String?> = MutableStateFlow(null)
        private set
    var isSearchLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set
    var isExploreLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set

    init {
        getApods(
            startDate = "2023-01-01",
            endDate = "2023-01-20",
            isForceRefresh = true
        )

        getApods(
            count = EXPLORE_PAGE_SIZE,
            isForceRefresh = true
        )
    }

    fun clearErrorState() {
        errorState.update { null }
    }

    fun getApods(
        startDate: String? = null,
        endDate: String? = null,
        count: Int? = null,
        isForceRefresh: Boolean = true
    ) {
        val isSearchCall = !startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()
        val isExploreCall = count != null
        if (isSearchCall) isSearchLoading.update { true } else if (isExploreCall) isExploreLoading.update { true }

        if (isForceRefresh) searchApods.update { emptyList() }

        viewModelScope.launch {
            try {
                val response = apodApi.getApods(
                    startDate = startDate,
                    endDate = endDate,
                    count = count
                )
                withContext(Dispatchers.Main) {
                    if (isSearchCall) isSearchLoading.update { false } else if (isExploreCall) isExploreLoading.update { false }
                    if (response.isSuccessful) {
                        if (isSearchCall) {
                            searchApods.update { apodList -> apodList + response.body().orEmpty() }
                        } else if (isExploreCall) {
                            exploreApods.update { apodList -> response.body().orEmpty() }
                        }
                    } else {
                        errorState.update { "Empty List Fetched" }
                    }
                }
            } catch (ex: Throwable) {
                errorState.update { ex.cause.toString() }
            }
        }
    }

    fun addToFavorites(apodItem: ApodItem) {
        savedApods.update { apodItems ->
            val updatedList = listOf(apodItem) + apodItems
            updatedList.toSet()
        }
    }

    fun removeFromFavorites(apodItem: ApodItem) {
        savedApods.update { apodItems ->
            val mutableApodList = apodItems.toMutableList()
            mutableApodList.remove(apodItem)
            mutableApodList.toSet()
        }
    }
}

fun getApodApi() = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApodApi::class.java)