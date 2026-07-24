package com.example.billtracker.data.repository

import android.content.Context
import com.example.billtracker.data.remote.BotHolidayApi
import com.example.billtracker.domain.model.Holiday
import com.example.billtracker.domain.repository.HolidayRepository
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class HolidayRepositoryImpl(
    private val api: BotHolidayApi,
    private val apiToken: String,
    context: Context
) : HolidayRepository {

    private val prefs = context.getSharedPreferences("bot_holiday_cache", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override suspend fun getHolidays(year: Int): List<Holiday> {
        readFromCache(year)?.let { return it }

        return try {
            val response = api.getHolidays(token = apiToken, year = year.toString())
            val holidays = response.result.data.map { dto ->
                Holiday(
                    date = dateFormat.parse(dto.Date)!!.time,
                    name = dto.HolidayDescriptionThai
                )
            }
            saveToCache(year, holidays)
            holidays
        } catch (e: Exception) {
            emptyList() 
        }
    }

    private fun cacheKey(year: Int) = "holidays_$year"

    private fun saveToCache(year: Int, holidays: List<Holiday>) {
        val array = JSONArray()
        holidays.forEach { holiday ->
            array.put(JSONObject().apply {
                put("date", holiday.date)
                put("name", holiday.name)
            })
        }
        prefs.edit().putString(cacheKey(year), array.toString()).apply()
    }

    private fun readFromCache(year: Int): List<Holiday>? {
        val jsonString = prefs.getString(cacheKey(year), null) ?: return null
        return try {
            val array = JSONArray(jsonString)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Holiday(date = obj.getLong("date"), name = obj.getString("name"))
            }
        } catch (e: Exception) {
            null
        }
    }
}