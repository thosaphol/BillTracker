package com.example.billtracker.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface BotHolidayApi {
    @GET("financial-institutions-holidays/")
    suspend fun getHolidays(
        @Header("Authorization") token: String,
        @Query("year") year: String
    ): BotHolidayResponse
}

data class BotHolidayResponse(
    val result: BotHolidayResult
)

data class BotHolidayResult(
    val api: String,
    val timestamp: String,
    val data: List<BotHolidayDto>
)


data class BotHolidayDto(
    val HolidayWeekDay: String,
    val HolidayWeekDayThai: String,
    val Date: String,           // "2026-01-01"
    val DateThai: String,       // "01/01/2569"
    val HolidayDescription: String,
    val HolidayDescriptionThai: String
)