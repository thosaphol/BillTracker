package com.example.billtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billtracker.common.Event
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.usecase.bill.GetAllBillsUseCase
import com.example.billtracker.domain.usecase.category.GetAllCategoriesUseCase
import com.example.billtracker.domain.usecase.bill.MarkBillAsPaidUseCase
import kotlinx.coroutines.launch

class BillListViewModel(
    private val getAllBillsUseCase: GetAllBillsUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val markBillAsPaidUseCase: MarkBillAsPaidUseCase
) : ViewModel() {

    // observe ตรงจาก use case เลย - Room จะคอย emit ค่าใหม่เองอัตโนมัติ (ดูที่คุยกันเรื่อง LiveData ก่อนหน้า)
    val bills: LiveData<List<Bill>> = getAllBillsUseCase()
    val categories: LiveData<List<Category>> = getAllCategoriesUseCase()

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    fun togglePaid(bill: Bill) {
        viewModelScope.launch {
            val result = markBillAsPaidUseCase(bill.id, isPaid = !bill.isPaid)
            result.onFailure { e ->
                _errorEvent.value = Event(e.message ?: "เกิดข้อผิดพลาด ลองใหม่อีกครั้ง")
            }
        }
    }

    class Factory(
        private val getAllBillsUseCase: GetAllBillsUseCase,
        private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
        private val markBillAsPaidUseCase: MarkBillAsPaidUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BillListViewModel(getAllBillsUseCase, getAllCategoriesUseCase, markBillAsPaidUseCase) as T
        }
    }
}