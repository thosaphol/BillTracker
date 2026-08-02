package com.example.billtracker.domain.usecase.holiday

import com.example.billtracker.domain.model.Holiday
import com.example.billtracker.domain.repository.HolidayRepository
import javax.inject.Inject

class GetHolidaysUseCase @Inject constructor (
    private val repository: HolidayRepository
) {
    suspend operator fun invoke(year: Int): Result<List<Holiday>> = repository.getHolidays(year)
}