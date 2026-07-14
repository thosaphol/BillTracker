package com.example.billtracker.domain.usecase.setting

import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository


/**
 * ลบข้อมูลทั้งหมด (PDPA) - ลบบิลทั้งหมด + category ที่ผู้ใช้เพิ่มเอง
 * (default category 5 อันคงอยู่เสมอ ไม่ถูกลบ)
 */
class DeleteAllDataUseCase(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(): Result<Unit> = try {
        billRepository.deleteAllBills()
        categoryRepository.deleteAllCustomCategories()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}