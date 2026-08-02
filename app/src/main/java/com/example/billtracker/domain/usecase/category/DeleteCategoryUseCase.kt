package com.example.billtracker.domain.usecase.category

import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject


class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val billRepository: BillRepository
) {
    suspend operator fun invoke(category: Category): Result<Unit> {
        if (!category.isCustom) {
            return Result.failure(IllegalStateException("ไม่สามารถลบหมวดหมู่เริ่มต้นได้"))
        }

        val billsUsingCategory = billRepository.getBillsByCategoryId(category.id)
        if (billsUsingCategory.isNotEmpty()) {
            return Result.failure(
                IllegalStateException(
                    "ไม่สามารถลบได้ เนื่องจากมีรายการค้างจ่าย ${billsUsingCategory.size} " +
                            "รายการใช้หมวดหมู่นี้อยู่ กรุณาย้ายหรือลบรายการเหล่านั้นก่อน"
                )
            )
        }

        return try {
            categoryRepository.deleteCategory(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

//class DeleteCategoryUseCase(
//    val categoryRepository: CategoryRepository,
//    val billRepository: BillRepository
//) {
//    suspend operator fun invoke(category: Category): Result<Unit> {
//        return try {
//            val billsUsingCategory = billRepository.getBillsByCategoryId(category.id)
//            if (billsUsingCategory.isNotEmpty()) {
//                return Result.failure(
//                    IllegalStateException("Cannot delete category in use by ${billsUsingCategory.size} bill(s)")
//                )
//            }
//            categoryRepository.deleteCategory(category)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}