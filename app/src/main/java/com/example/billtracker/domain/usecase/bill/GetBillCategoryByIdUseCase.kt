package com.example.billtracker.domain.usecase.bill

import com.example.billtracker.domain.model.BillCategory
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject

class GetBillCategoryByIdUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val billRepository: BillRepository
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