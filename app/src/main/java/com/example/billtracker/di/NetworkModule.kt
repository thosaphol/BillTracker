package com.example.billtracker.di

import com.example.billtracker.BuildConfig
import com.example.billtracker.data.remote.BotHolidayApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @BotApiToken
    fun provideBotApiToken(): String = BuildConfig.BOT_API_TOKEN

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideBotHolidayApi(client: OkHttpClient): BotHolidayApi =
        Retrofit.Builder()
            .baseUrl("https://gateway.api.bot.or.th/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BotHolidayApi::class.java)
}