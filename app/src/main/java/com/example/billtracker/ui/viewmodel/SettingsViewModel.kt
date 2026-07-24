package com.example.billtracker.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.billtracker.common.Event
import com.example.billtracker.domain.usecase.setting.DeleteAllDataUseCase
import com.example.billtracker.domain.usecase.setting.ExportDataUseCase
import com.example.billtracker.domain.usecase.setting.ImportDataUseCase
import kotlinx.coroutines.launch

sealed interface SettingsEvent {
    data object ExportSuccess : SettingsEvent
    data object ImportSuccess : SettingsEvent
    data object DeleteAllSuccess : SettingsEvent
    data class Error(val message: String) : SettingsEvent
}

class SettingsViewModel(
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val deleteAllDataUseCase: DeleteAllDataUseCase
) : ViewModel() {

    private val _events = MutableLiveData<Event<SettingsEvent>>()
    val events: LiveData<Event<SettingsEvent>> = _events

    fun exportData(password: String) {
        viewModelScope.launch {
            val result = exportDataUseCase(password)
            result.fold(
                onSuccess = { _events.value = Event(SettingsEvent.ExportSuccess) },
                onFailure = { e -> _events.value = Event(SettingsEvent.Error(e.message ?: "ส่งออกข้อมูลไม่สำเร็จ")) }
            )
        }
    }

    fun importData(uri: Uri, password: String) {
        viewModelScope.launch {
            val result = importDataUseCase(uri, password)
            result.fold(
                onSuccess = { _events.value = Event(SettingsEvent.ImportSuccess) },
                onFailure = { e -> _events.value = Event(SettingsEvent.Error(e.message ?: "นำเข้าข้อมูลไม่สำเร็จ")) }
            )
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            val result = deleteAllDataUseCase()
            result.fold(
                onSuccess = { _events.value = Event(SettingsEvent.DeleteAllSuccess) },
                onFailure = { e -> _events.value = Event(SettingsEvent.Error(e.message ?: "ลบข้อมูลไม่สำเร็จ")) }
            )
        }
    }

    class Factory(
        private val exportDataUseCase: ExportDataUseCase,
        private val importDataUseCase: ImportDataUseCase,
        private val deleteAllDataUseCase: DeleteAllDataUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(exportDataUseCase, importDataUseCase, deleteAllDataUseCase) as T
        }
    }
}