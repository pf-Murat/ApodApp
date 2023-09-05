package com.example.apodapp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.apodapp.data.ApodApi
import com.example.apodapp.data.ApodItem
import com.example.apodapp.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

const val PAGE_SIZE = 5

val calendarStart = Calendar.getInstance()

val calendarEnd = Calendar.getInstance()

private const val BASE_URL = "https://api.nasa.gov/planetary/"
private const val EXPLORE_PAGE_SIZE = 50

class MainViewModel(
    val context: Context
) : ViewModel() {
    private val apodApi by lazy { getApodApi() }
    private val apodDb by lazy { getRoomDatabase(context = context) }
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
        calendarStart.add(Calendar.DATE, -PAGE_SIZE)
        calendarEnd.add(Calendar.DATE, -1)
        getApods(isForceRefresh = true)

        getApods(
            count = EXPLORE_PAGE_SIZE,
            isForceRefresh = true
        )
        subscribeToSavedApods()

    }

    private fun subscribeToSavedApods() {
        viewModelScope.launch {
            apodDb.apodDao().getAll().collectLatest { savedApodList ->
                savedApods.update { savedApodList.toSet() }
                val savedIds = savedApodList.map { it.date }
                val updatedSearchApodsWithSave = searchApods.value.toMutableList().map { apod ->
                    apod.copy(isFavorited = savedIds.contains(apod.date))
                }
                searchApods.update {
                    updatedSearchApodsWithSave
                }
            }
        }
    }

    fun clearErrorState() {
        errorState.update { null }
    }

    fun getApods(isForceRefresh: Boolean = false) {
        if (!isForceRefresh) {
            calendarStart.add(Calendar.DATE, -PAGE_SIZE)
            calendarEnd.add(Calendar.DATE, -PAGE_SIZE)
        }
        getApods(
            startDate = calendarStart.getBackEndTime(),
            endDate = calendarEnd.getBackEndTime(),
            isForceRefresh = isForceRefresh
        )
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
                            searchApods.update { apodList ->
                                apodList + response.body().orEmpty().reversed()
                            }
                        } else if (isExploreCall) {
                            exploreApods.update { apodList -> response.body().orEmpty() }
                        }
                        subscribeToSavedApods()
                    } else {
                        errorState.update { "Response was not successful" }
                    }
                }
            } catch (ex: Throwable) {
                errorState.update { ex.cause.toString() }
            }
        }
    }

    fun addToFavorites(apodItem: ApodItem) {
        viewModelScope.launch {
            apodDb.apodDao().insertAll(apodItem.copy(isFavorited = true))
        }
        val updatedList =
            searchApods.value.map { apod ->
                if (apod.date == apodItem.date) {
                    apod.copy(isFavorited = true)
                } else {
                    apod
                }
            }
        searchApods.update { updatedList }
    }

    fun removeFromFavorites(apodItem: ApodItem) {
        viewModelScope.launch {
            apodDb.apodDao().delete(apodItem)
        }
        val updatedListItem =
            searchApods.value.map { apod ->
                if (apod.date == apodItem.date) {
                    apod.copy(isFavorited = false)
                } else {
                    apod
                }
            }
        searchApods.update { updatedListItem }
    }

    fun removeFromExplores(apodItem: ApodItem) {
        val updatedList = exploreApods.value.toMutableList().filter { it.date != apodItem.date }
        exploreApods.update { apodItems ->
            updatedList
        }
    }

    fun removeFromExploresAndAddToFavs(apodItem: ApodItem) {
        removeFromExplores(apodItem)
        addToFavorites(apodItem)
    }
}

fun getApodApi() =
    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        .create(ApodApi::class.java)

fun getRoomDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java, "database-apod"
    ).build()
}


fun Calendar.getBackEndTime() =
    "${this[Calendar.YEAR]}-${this[Calendar.MONTH].inc()}-${this[Calendar.DAY_OF_MONTH]}"