package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.CategoryRepository


/**
 * เพิ่มหมวดหมู่ใหม่ (custom เท่านั้น - default 5 อันมีอยู่แล้วตั้งแต่ seed DB)
 * validation: ชื่อห้ามว่าง
 *
 * หมายเหตุ: ไม่เช็ค "ชื่อซ้ำ" เพราะ category ที่เลือกใช้จริงมาจาก
 * radio button ที่ populate จาก DB อยู่แล้ว (ตามที่เคยตัดสินใจไว้ว่า
 * เป็น redundant check ถ้าผู้ใช้เลือกจาก list ที่มีอยู่จริงเสมอ)
 */
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