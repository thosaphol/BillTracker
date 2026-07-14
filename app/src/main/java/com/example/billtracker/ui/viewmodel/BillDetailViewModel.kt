package com.example.billtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billtracker.common.Event
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.usecase.bill.DeleteBillUseCase
import com.example.billtracker.domain.usecase.bill.GetBillByIdUseCase
import com.example.billtracker.domain.usecase.category.GetCategoryByIdUseCase
import com.example.billtracker.domain.usecase.bill.MarkBillAsPaidUseCase
import kotlinx.coroutines.launch

sealed interface BillDetailEvent {
    data object DeletedSuccessfully : BillDetailEvent
    data class Error(val message: String) : BillDetailEvent
}

/**
 * ใช้กับ BillDetailScreen
 * ไม่ใช้ Hilt - inject use case ผ่าน constructor ธรรมดา
 */
class BillDetailViewModel(
    private val getBillByIdUseCase: GetBillByIdUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val markBillAsPaidUseCase: MarkBillAsPaidUseCase,
    private val deleteBillUseCase: DeleteBillUseCase
) : ViewModel() {

    private val _bill = MutableLiveData<Bill?>()
    val bill: LiveData<Bill?> = _bill

    private val _category = MutableLiveData<Category?>()
    val category: LiveData<Category?> = _category

    private val _events = MutableLiveData<Event<BillDetailEvent>>()
    val events: LiveData<Event<BillDetailEvent>> = _events

    /** เรียกตอนเปิดหน้านี้ (จาก LaunchedEffect(billId) ใน NavHost) */
    fun load(billId: Int) {
        viewModelScope.launch {
            val loadedBill = getBillByIdUseCase(billId)
            _bill.value = loadedBill
            if (loadedBill != null) {
                _category.value = getCategoryByIdUseCase(loadedBill.categoryId)
            }
        }
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
        private val deleteBillUseCase: DeleteBillUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BillDetailViewModel(
                getBillByIdUseCase, getCategoryByIdUseCase, markBillAsPaidUseCase, deleteBillUseCase
            ) as T
        }
    }
}