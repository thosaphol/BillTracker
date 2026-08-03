package com.example.billtracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billtracker.common.Event
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.usecase.category.AddCategoryUseCase
import com.example.billtracker.domain.usecase.category.DeleteCategoryUseCase
import com.example.billtracker.domain.usecase.category.GetAllCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryManageViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    val categories: LiveData<List<Category>> = getAllCategoriesUseCase()

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    fun addCategory(name: String) {
        viewModelScope.launch {
            val result = addCategoryUseCase(name = name, iconKey = "more_horiz")
            result.onFailure { e ->
                _errorEvent.value = Event(e.message ?: "เพิ่มหมวดหมู่ไม่สำเร็จ")
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            val result = deleteCategoryUseCase(category)
            result.onFailure { e ->
                // ครอบคลุมทั้ง 2 เคส: isCustom == false และมีบิลผูกอยู่ (ข้อความมาจาก use case โดยตรง)
                _errorEvent.value = Event(e.message ?: "ลบหมวดหมู่ไม่สำเร็จ")
            }
        }
    }

    class Factory(
        private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
        private val addCategoryUseCase: AddCategoryUseCase,
        private val deleteCategoryUseCase: DeleteCategoryUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryManageViewModel(getAllCategoriesUseCase, addCategoryUseCase, deleteCategoryUseCase) as T
        }
    }
}