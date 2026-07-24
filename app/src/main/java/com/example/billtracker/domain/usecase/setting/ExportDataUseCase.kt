package com.example.billtracker.domain.usecase.setting

import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import com.example.billtracker.domain.repository.DataExporter


class ExportDataUseCase(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository,
    private val dataExporter: DataExporter
) {
    suspend operator fun invoke(password: String): Result<Unit> {
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร"))
        }
        val bills = billRepository.getAllBillsOnce()
        val categories = categoryRepository.getAllCategoriesOnce()
        return dataExporter.export(bills, categories, password)
    }
}