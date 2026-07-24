package com.example.billtracker.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY is_custom ASC, name ASC")
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Int): CategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Query("SELECT * FROM categories ORDER BY is_custom ASC, name ASC")
    suspend fun getAllCategoriesOnce(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>) : Unit

    @Update
    suspend fun update(category: CategoryEntity): Unit

    @Delete
    suspend fun delete(category: CategoryEntity): Unit

    @Query("DELETE FROM categories WHERE is_custom = 1")
    suspend fun deleteAllCustom(): Unit
}