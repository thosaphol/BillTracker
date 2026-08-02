package com.example.billtracker.domain.usecase.setting

import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject


class DeleteAllDataUseCase @Inject constructor(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): Result<Unit> = try {
        billRepository.deleteAllBills()
        categoryRepository.deleteAllCustomCategories()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}