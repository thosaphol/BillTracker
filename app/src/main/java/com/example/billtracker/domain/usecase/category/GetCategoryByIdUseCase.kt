package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository

class GetCategoryByIdUseCase(
    val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Int): Category? {
        return categoryRepository.getCategoryById(id)
    }
}