package com.example.billtracker.domain.repository

import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category


interface DataExporter {
    suspend fun export(bills: List<Bill>, categories: List<Category>): Result<Unit>
}