package com.finverse.di

import android.content.Context
import androidx.room.Room
import com.finverse.data.local.AppDatabase
import com.finverse.data.repository.FinancialRepository
import com.finverse.domain.FinancialCalculationEngine
import com.finverse.network.AIApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val financialRepository: FinancialRepository
    val calculationEngine: FinancialCalculationEngine
    val aiApiService: AIApiService
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finverse_db"
        ).fallbackToDestructiveMigration().build()
    }

    override val financialRepository: FinancialRepository by lazy {
        FinancialRepository(database.financialDao())
    }

    override val calculationEngine: FinancialCalculationEngine by lazy {
        FinancialCalculationEngine()
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl("https://bol-ai-live-backend1.vercel.app/")
        .client(okHttpClient)
        .build()

    override val aiApiService: AIApiService by lazy {
        retrofit.create(AIApiService::class.java)
    }
}
