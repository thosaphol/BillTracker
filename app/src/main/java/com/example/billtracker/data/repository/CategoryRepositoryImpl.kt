package com.example.billtracker.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.billtracker.data.local.CategoryDao
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): LiveData<List<Category>> =
        categoryDao.getAllCategories().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Int): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    override suspend fun addCategory(category: Category): Long =
        categoryDao.insert(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.update(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        categoryDao.delete(category.toEntity())

    override suspend fun getCategoryByName(name: String) =
        categoryDao.getCategoryByName(name)?.toDomain()


    override suspend fun deleteAllCustomCategories() =
        categoryDao.deleteAllCustom()

    override suspend fun getAllCategoriesOnce(): List<Category> = categoryDao.getAllCategoriesOnce().map { it.toDomain() }
}