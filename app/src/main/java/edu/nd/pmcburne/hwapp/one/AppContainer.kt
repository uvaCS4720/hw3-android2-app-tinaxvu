package edu.nd.pmcburne.hwapp.one

import android.content.Context
import androidx.room.Room
import edu.nd.pmcburne.hwapp.one.local.BasketballDatabase
import edu.nd.pmcburne.hwapp.one.remote.BasketballApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://ncaa-api.henrygd.me/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(BasketballApiService::class.java)

    private val database = Room.databaseBuilder(
        context.applicationContext,
        BasketballDatabase::class.java,
        "basketball_db"
    ).build()

    val repository = BasketballRepository(apiService, database.gameDao(), context.applicationContext)
}