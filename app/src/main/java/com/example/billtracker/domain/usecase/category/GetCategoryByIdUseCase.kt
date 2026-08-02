package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: Int): Category? {
        return categoryRepository.getCategoryById(id)
    }
}