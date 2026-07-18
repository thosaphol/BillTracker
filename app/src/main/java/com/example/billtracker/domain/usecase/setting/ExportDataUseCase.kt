package com.example.billtracker.domain.usecase.setting

import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import com.example.billtracker.domain.repository.DataExporter

class ExportDataUseCase(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository,
    private val dataExporter: DataExporter
) {
    suspend operator fun invoke(): Result<Unit> {
        val bills = billRepository.getAllBills().value.orEmpty()
        val categories = categoryRepository.getAllCategories().value.orEmpty()
        return dataExporter.export(bills, categories)
    }
}