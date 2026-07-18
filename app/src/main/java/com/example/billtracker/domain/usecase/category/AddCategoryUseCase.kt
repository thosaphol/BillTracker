package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository

class AddCategoryUseCase (
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

//class AddCategoryUseCase(
//    val categoryRepository: CategoryRepository
//) {
//
//    suspend operator fun invoke(newCategory: Category): Result<Long> {
//        val existingCategory = categoryRepository.getCategoryByName(newCategory.name)
//        if (existingCategory != null) {
//            return Result.failure(IllegalStateException("Category name already exists"))
//        }
//        val newId = categoryRepository.addCategory(newCategory)
//        return Result.success(newId)
//    }
//}