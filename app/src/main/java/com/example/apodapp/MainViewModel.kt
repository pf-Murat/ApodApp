package com.example.apodapp

import androidx.lifecycle.ViewModel
import com.example.apodapp.ui.theme.ApodApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL =  "https://api.nasa.gov/planetary/"

class MainViewModel : ViewModel() {

    val apodApi by lazy { getApodApi() }

    fun generateImageList(): List<String> {
        val list = mutableListOf<String>()
        repeat(10){ counter ->
            list.add("Sample $counter")
        }
        return list.toList()
    }
}

fun getApodApi() = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build().create(ApodApi::class.java)