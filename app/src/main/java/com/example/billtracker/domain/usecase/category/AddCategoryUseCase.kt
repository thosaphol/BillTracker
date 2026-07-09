package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository

class AddCategoryUseCase(
    val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(newCategory: Category): Result<Long> {
        val existingCategory = categoryRepository.getCategoryByName(newCategory.name)
        if (existingCategory != null) {
            return Result.failure(IllegalStateException("Category name already exists"))
        }
        val newId = categoryRepository.addCategory(newCategory)
        return Result.success(newId)
    }
}