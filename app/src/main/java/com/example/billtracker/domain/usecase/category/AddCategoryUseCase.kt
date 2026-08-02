package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(name: String, iconKey: String): Result<Long> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("กรุณาระบุชื่อหมวดหมู่"))
        }
        return try {
            val category = Category(name = name.trim(), iconKey = iconKey, isCustom = true)
            Result.success(repository.addCategory(category))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}