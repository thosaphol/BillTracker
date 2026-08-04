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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillListViewModel @Inject constructor (
    private val getAllBillsUseCase: GetAllBillsUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val markBillAsPaidUseCase: MarkBillAsPaidUseCase
) : ViewModel() {

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
}