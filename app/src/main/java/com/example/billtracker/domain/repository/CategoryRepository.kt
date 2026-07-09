package com.example.billtracker.domain.repository

import androidx.lifecycle.LiveData
import com.example.billtracker.domain.model.Category

interface CategoryRepository {
    fun getAllCategories(): LiveData<List<Category>>
    suspend fun getCategoryById(id: Int): Category?
    suspend fun getCategoryByName(name: String): Category?
    suspend fun updateCategory(category: Category)
    suspend fun addCategory(category: Category): Long
    suspend fun deleteCategory(category: Category)
}