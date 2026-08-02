package com.example.billtracker.domain.usecase.category

import androidx.lifecycle.LiveData
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor (
    private val categoryRepository: CategoryRepository) {
    operator fun invoke(): LiveData<List<Category>> {
        return categoryRepository.getAllCategories()
    }
}