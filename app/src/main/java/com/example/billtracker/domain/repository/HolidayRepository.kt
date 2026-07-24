package com.example.billtracker.domain.repository

import com.example.billtracker.domain.model.Holiday


interface HolidayRepository {
    suspend fun getHolidays(year: Int): List<Holiday>
}