package com.example.billtracker.domain.usecase.holiday

import com.example.billtracker.domain.model.Holiday
import com.example.billtracker.domain.repository.HolidayRepository

class GetHolidaysUseCase(
    private val repository: HolidayRepository
) {
    suspend operator fun invoke(year: Int): List<Holiday> {
        return repository.getHolidays(year)
    }
}