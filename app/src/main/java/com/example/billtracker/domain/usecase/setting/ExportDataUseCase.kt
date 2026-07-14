package com.example.billtracker.domain.usecase.setting

import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import com.example.billtracker.domain.repository.DataExporter

/**
 * ดึงข้อมูลปัจจุบันทั้งหมด (ครั้งเดียว ไม่ observe) แล้วส่งให้ DataExporter เขียนไฟล์
 * หมายเหตุ: ใช้ .getAllBills()/.getAllCategories() (LiveData) แล้วอ่านค่า .value
 * ครั้งเดียวผ่าน helper ด้านล่าง เพราะ export เป็น one-time action ไม่ใช่ observe ต่อเนื่อง
 */
class ExportDataUseCase(
    private val billRepository: BillRepository,
    private val categoryRepository: CategoryRepository,
    private val dataExporter: DataExporter
) {
    suspend operator fun invoke(): Result<Unit> {
        val bills = billRepository.getAllBills().value.orEmpty()
        val categories = categoryRepository.getAllCategories().value.orEmpty()
        return dataExporter.export(bills, categories)
    }
}