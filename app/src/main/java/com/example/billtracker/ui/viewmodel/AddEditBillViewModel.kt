package com.example.billtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billtracker.common.Event
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.domain.usecase.bill.AddBillUseCase
import com.example.billtracker.domain.usecase.category.GetAllCategoriesUseCase
import com.example.billtracker.domain.usecase.bill.GetBillByIdUseCase
import com.example.billtracker.domain.usecase.bill.UpdateBillUseCase
import com.example.billtracker.ui.screens.BillFormState
import kotlinx.coroutines.launch

sealed interface SaveBillResult {
    data object Success : SaveBillResult
    data class Error(val message: String) : SaveBillResult
}

class AddEditBillViewModel(
    private val getBillByIdUseCase: GetBillByIdUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addBillUseCase: AddBillUseCase,
    private val updateBillUseCase: UpdateBillUseCase
) : ViewModel() {

    private var editingBillId: Int? = null
    private var originalCreatedAt: Long = System.currentTimeMillis()

    val isEditMode: Boolean get() = (editingBillId ?: 0) > 0

    private val _formState = MutableLiveData(BillFormState())
    val formState: LiveData<BillFormState> = _formState

    val categories: LiveData<List<Category>> = getAllCategoriesUseCase()

    private val _saveEvent = MutableLiveData<Event<SaveBillResult>>()
    val saveEvent: LiveData<Event<SaveBillResult>> = _saveEvent


    fun load(billId: Int) {
        editingBillId = billId
        viewModelScope.launch {
            val bill = getBillByIdUseCase(billId)
            if (bill != null) {
                originalCreatedAt = bill.createdAt
                _formState.value = BillFormState(
                    title = bill.title,
                    amount = bill.amount.toString(),
                    categoryId = bill.categoryId,
                    dueDate = bill.dueDate,
                    reminderEnabled = bill.reminderType != ReminderType.NONE,
                    reminderType = bill.reminderType,
                    reminderStartDate = bill.reminderStartDate,
                    note = bill.note
                )
            } else {
                _saveEvent.value = Event(SaveBillResult.Error("ไม่พบรายการที่ต้องการแก้ไข"))
            }
        }
    }

    fun onTitleChange(value: String) {
        _formState.value = _formState.value?.copy(title = value)
    }

    fun onAmountChange(value: String) {
        _formState.value = _formState.value?.copy(amount = value)
    }

    fun onCategorySelect(category: Category) {
        _formState.value = _formState.value?.copy(categoryId = category.id)
    }


    fun onDueDateSelected(millis: Long) {
        _formState.value = _formState.value?.copy(dueDate = millis)
    }

    fun onReminderEnabledChange(enabled: Boolean) {
        _formState.value = _formState.value?.copy(
            reminderEnabled = enabled,
            reminderType = if (enabled) _formState.value?.reminderType ?: ReminderType.NONE else ReminderType.NONE
        )
    }

    fun onReminderTypeChange(type: ReminderType) {
        _formState.value = _formState.value?.copy(reminderType = type)
    }

    fun onReminderStartDateSelected(millis: Long) {
        _formState.value = _formState.value?.copy(reminderStartDate = millis)
    }

    fun onNoteChange(value: String) {
        _formState.value = _formState.value?.copy(note = value)
    }

    fun save() {
        val state = _formState.value ?: return

        val amountValue = state.amount.toDoubleOrNull()
        if (amountValue == null) {
            _saveEvent.value = Event(SaveBillResult.Error("จำนวนเงินไม่ถูกต้อง"))
            return
        }
        val categoryId = state.categoryId
        if (categoryId == null) {
            _saveEvent.value = Event(SaveBillResult.Error("กรุณาเลือกประเภท"))
            return
        }
        val dueDate = state.dueDate
        if (dueDate == null) {
            _saveEvent.value = Event(SaveBillResult.Error("กรุณาเลือกวันครบกำหนด"))
            return
        }

        val bill = Bill(
            id = editingBillId ?: 0,
            title = state.title.trim(),
            amount = amountValue,
            dueDate = dueDate,
            categoryId = categoryId,
            isPaid = false,
            note = state.note.trim(),
            reminderType = if (state.reminderEnabled) state.reminderType else ReminderType.NONE,
            reminderStartDate = if (state.reminderEnabled) state.reminderStartDate else null,
            createdAt = originalCreatedAt
        )

        viewModelScope.launch {
            val result = if (isEditMode) updateBillUseCase(bill) else addBillUseCase(bill).map { }
            result.fold(
                onSuccess = { _saveEvent.value = Event(SaveBillResult.Success) },
                onFailure = { e ->
                    _saveEvent.value = Event(SaveBillResult.Error(e.message ?: "บันทึกไม่สำเร็จ"))
                }
            )
        }
    }

    class Factory(
        private val getBillByIdUseCase: GetBillByIdUseCase,
        private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
        private val addBillUseCase: AddBillUseCase,
        private val updateBillUseCase: UpdateBillUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddEditBillViewModel(
                getBillByIdUseCase, getAllCategoriesUseCase, addBillUseCase, updateBillUseCase
            ) as T
        }
    }
}