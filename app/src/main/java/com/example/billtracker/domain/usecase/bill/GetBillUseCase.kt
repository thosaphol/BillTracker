package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.model.BillCategory
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository

class GetBillUseCase(
    val categoryRepository: CategoryRepository,
    val billRepository: BillRepository
) {

    suspend operator fun invoke(id: Int): BillCategory?{
        val bill = billRepository.getBillById(id) ?: return null
        val category = categoryRepository.getCategoryById(bill.categoryId) ?: return null
        return BillCategory(
            bill = bill,
            category = category
        )
    }
}