package com.example.apodapp.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
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

@Entity
data class ApodItem(
    @PrimaryKey
    val date: String = "",
    val copyright: String = "",
    val explanation: String = "",
    @SerializedName("media_type")
    val mediaType: String = "",
    @SerializedName("service_version")
    val serviceVersion: String = "",
    val title: String = "",
    val url: String = "",
    @SerializedName("hdurl")
    val hdUrl: String = "",
    val isFavorited: Boolean = false
)

@Database(entities = [ApodItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
}

