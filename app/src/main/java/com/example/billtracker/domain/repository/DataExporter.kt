package com.example.billtracker.domain.repository

import android.net.Uri
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category


interface DataExporter {

    suspend fun export(bills: List<Bill>, categories: List<Category>, password: String): Result<Unit>
    suspend fun import(uri: Uri, password: String): Result<ImportedData>
}

data class ImportedData(
    val bills: List<Bill>,
    val categories: List<Category>
)