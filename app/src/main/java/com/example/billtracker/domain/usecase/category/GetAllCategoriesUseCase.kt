package com.example.billtracker.domain.usecase.category

import androidx.lifecycle.LiveData
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository

class GetAllCategoriesUseCase(val categoryRepository: CategoryRepository) {
    operator fun invoke(): LiveData<List<Category>> {
        return categoryRepository.getAllCategories()
    }
}