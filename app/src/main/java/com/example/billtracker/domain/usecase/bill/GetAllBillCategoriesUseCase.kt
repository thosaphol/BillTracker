package com.example.billtracker.domain.usecase.bill

import androidx.lifecycle.LiveData
import com.example.billtracker.common.combineLatest
import com.example.billtracker.domain.model.BillCategory
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import javax.inject.Inject

class GetAllBillCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val billRepository: BillRepository
) {

    operator fun invoke(): LiveData<List<BillCategory>> {

        val categories = categoryRepository.getAllCategories()
        val bills = billRepository.getAllBills()
        return combineLatest(categories, bills) { categories, bills ->
            val categoriesMap = categories.associateBy { it.id }
            bills.mapNotNull { bill ->
                categoriesMap[bill.categoryId]?.let { category ->
                    BillCategory(
                        bill = bill,
                        category = category
                    )
                }

            }
        }
    }

}
