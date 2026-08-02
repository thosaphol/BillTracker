package com.example.billtracker.domain.usecase.setting

import android.net.Uri
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import com.example.billtracker.domain.repository.DataExporter
import javax.inject.Inject


class ImportDataUseCase @Inject constructor(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository,
    private val dataExporter: DataExporter
) {
    suspend operator fun invoke(uri: Uri, password: String): Result<Unit> {
        val importResult = dataExporter.import(uri, password)
        val imported = importResult.getOrElse { return Result.failure(it) }

        return try {
            imported.categories.forEach { category -> categoryRepository.addCategory(category) }
            imported.bills.forEach { bill -> billRepository.addBill(bill) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}