package com.example.apodapp.ui.theme

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "UAaJXjREfYN8FSIWfjblkEeDvangZpMp6NfDdvVZ"

interface ApodApi {
    @GET("apod")
    suspend fun getApods(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("date") date: String? = null,
        @Query("start_date") startDate: String? = "2023-07-10",
        @Query("end_date") endDate: String? = "2023-08-02",
        @Query("count") count: Int? = null,
    ): Response<List<ApodItem>>
}

data class ApodItem(
    val copyright: String,
    val date: String,
    val explanation: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("service_version")
    val serviceVersion: String,
    val title: String,
    val url: String,
    @SerializedName("hdurl")
    val hdUrl: String
)