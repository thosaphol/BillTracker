package com.example.billtracker.domain.usecase.bill

import androidx.lifecycle.LiveData
import com.example.billtracker.common.combineLatest
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.BillCategory
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository

class GetAllBillsUseCase (
    val billRepository: BillRepository
) {

    operator fun invoke(): LiveData<List<Bill>> {
        return billRepository.getAllBills()
    }

}