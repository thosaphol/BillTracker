package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository

class DeleteCategoryUseCase(
    val categoryRepository: CategoryRepository,
    val billRepository: BillRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        return try {
            val billsUsingCategory = billRepository.getBillsByCategoryId(category.id)
            if (billsUsingCategory.isNotEmpty()) {
                return Result.failure(
                    IllegalStateException("Cannot delete category in use by ${billsUsingCategory.size} bill(s)")
                )
            }
            categoryRepository.deleteCategory(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}