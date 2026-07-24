package com.example.billtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billtracker.common.Event
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.model.Holiday
import com.example.billtracker.domain.model.matchingWithinDays
import com.example.billtracker.domain.usecase.bill.DeleteBillUseCase
import com.example.billtracker.domain.usecase.bill.GetBillByIdUseCase
import com.example.billtracker.domain.usecase.bill.MarkBillAsPaidUseCase
import com.example.billtracker.domain.usecase.category.GetCategoryByIdUseCase
import com.example.billtracker.domain.usecase.holiday.GetHolidaysUseCase
import kotlinx.coroutines.launch
import java.util.Calendar

sealed interface BillDetailEvent {
    data object DeletedSuccessfully : BillDetailEvent
    data class Error(val message: String) : BillDetailEvent
}

class BillDetailViewModel(
    private val getBillByIdUseCase: GetBillByIdUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val markBillAsPaidUseCase: MarkBillAsPaidUseCase,
    private val deleteBillUseCase: DeleteBillUseCase,
    private val getHolidaysUseCase: GetHolidaysUseCase
) : ViewModel() {

    private val _bill = MutableLiveData<Bill?>()
    val bill: LiveData<Bill?> = _bill

    private val _category = MutableLiveData<Category?>()
    val category: LiveData<Category?> = _category

    private val _holidayWarning = MutableLiveData<List<Holiday>>(emptyList())
    val holidayWarning: LiveData<List<Holiday>> = _holidayWarning

    private val _events = MutableLiveData<Event<BillDetailEvent>>()
    val events: LiveData<Event<BillDetailEvent>> = _events

    fun load(billId: Int) {
        viewModelScope.launch {
            val loadedBill = getBillByIdUseCase(billId)
            _bill.value = loadedBill
            if (loadedBill != null) {
                _category.value = getCategoryByIdUseCase(loadedBill.categoryId)
                checkHoliday(loadedBill.dueDate)
            }
        }
    }

    private suspend fun checkHoliday(dueDate: Long) {
        val year = Calendar.getInstance().apply { timeInMillis = dueDate }.get(Calendar.YEAR)
        val holidays = getHolidaysUseCase(year)
        val holidaysPrevYear = if (Calendar.getInstance().apply { timeInMillis = dueDate }
                .get(Calendar.DAY_OF_YEAR) <= 3) getHolidaysUseCase(year - 1) else emptyList()
        _holidayWarning.value = (holidays + holidaysPrevYear).matchingWithinDays(dueDate)
    }

    fun markAsPaid() {
        val current = _bill.value ?: return
        viewModelScope.launch {
            val result = markBillAsPaidUseCase(current.id, isPaid = true)
            result.onSuccess {
                _bill.value = current.copy(isPaid = true)
            }.onFailure { e ->
                _events.value = Event(BillDetailEvent.Error(e.message ?: "เกิดข้อผิดพลาด"))
            }
        }
    }

    fun delete() {
        val current = _bill.value ?: return
        viewModelScope.launch {
            val result = deleteBillUseCase(current)
            result.fold(
                onSuccess = { _events.value = Event(BillDetailEvent.DeletedSuccessfully) },
                onFailure = { e -> _events.value = Event(BillDetailEvent.Error(e.message ?: "ลบไม่สำเร็จ")) }
            )
        }
    }

    class Factory(
        private val getBillByIdUseCase: GetBillByIdUseCase,
        private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
        private val markBillAsPaidUseCase: MarkBillAsPaidUseCase,
        private val deleteBillUseCase: DeleteBillUseCase,
        private val getHolidaysUseCase: GetHolidaysUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BillDetailViewModel(
                getBillByIdUseCase, getCategoryByIdUseCase, markBillAsPaidUseCase,
                deleteBillUseCase, getHolidaysUseCase
            ) as T
        }
    }
}