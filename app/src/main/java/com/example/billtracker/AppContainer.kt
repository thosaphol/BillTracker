package com.example.billtracker

import android.content.Context
import com.example.billtracker.data.export.JsonDataExporter
import com.example.billtracker.data.local.AppDatabase
import com.example.billtracker.data.repository.BillRepositoryImpl
import com.example.billtracker.data.repository.CategoryRepositoryImpl
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import com.example.billtracker.domain.repository.DataExporter
import com.example.billtracker.domain.usecase.bill.AddBillUseCase
import com.example.billtracker.domain.usecase.bill.DeleteBillUseCase
import com.example.billtracker.domain.usecase.bill.GetAllBillsUseCase
import com.example.billtracker.domain.usecase.bill.GetBillByIdUseCase
import com.example.billtracker.domain.usecase.bill.MarkBillAsPaidUseCase
import com.example.billtracker.domain.usecase.bill.UpdateBillUseCase
import com.example.billtracker.domain.usecase.category.AddCategoryUseCase
import com.example.billtracker.domain.usecase.category.DeleteCategoryUseCase
import com.example.billtracker.domain.usecase.category.GetAllCategoriesUseCase
import com.example.billtracker.domain.usecase.category.GetCategoryByIdUseCase
import com.example.billtracker.domain.usecase.setting.DeleteAllDataUseCase
import com.example.billtracker.domain.usecase.setting.ExportDataUseCase


class AppContainer(private val context: Context) {

    // ---------- Data layer ----------
    private val database: AppDatabase by lazy { AppDatabase.getInstance(context) }

    private val billRepository: BillRepository by lazy { BillRepositoryImpl(database.billDao()) }
    private val categoryRepository: CategoryRepository by lazy { CategoryRepositoryImpl(database.categoryDao()) }
    private val dataExporter: DataExporter by lazy { JsonDataExporter(context) }

    // ---------- Bill use cases ----------
    val getAllBillsUseCase: GetAllBillsUseCase by lazy { GetAllBillsUseCase(billRepository) }
    val getBillByIdUseCase: GetBillByIdUseCase by lazy { GetBillByIdUseCase(billRepository) }
    val addBillUseCase: AddBillUseCase by lazy { AddBillUseCase(billRepository) }
    val updateBillUseCase: UpdateBillUseCase by lazy { UpdateBillUseCase(billRepository) }
    val deleteBillUseCase: DeleteBillUseCase by lazy { DeleteBillUseCase(billRepository) }
    val markBillAsPaidUseCase: MarkBillAsPaidUseCase by lazy { MarkBillAsPaidUseCase(billRepository) }

    // ---------- Category use cases ----------
    val getAllCategoriesUseCase: GetAllCategoriesUseCase by lazy { GetAllCategoriesUseCase(categoryRepository) }
    val getCategoryByIdUseCase: GetCategoryByIdUseCase by lazy { GetCategoryByIdUseCase(categoryRepository) }
    val addCategoryUseCase: AddCategoryUseCase by lazy { AddCategoryUseCase(categoryRepository) }
//    val updateCategoryUseCase: UpdateCategoryUseCase by lazy { UpdateCategoryUseCase(categoryRepository) }
    val deleteCategoryUseCase: DeleteCategoryUseCase by lazy {
        DeleteCategoryUseCase(categoryRepository, billRepository)
    }

    // ---------- Settings use cases ----------
    val exportDataUseCase: ExportDataUseCase by lazy {
        ExportDataUseCase(billRepository, categoryRepository, dataExporter)
    }
    val deleteAllDataUseCase: DeleteAllDataUseCase by lazy {
        DeleteAllDataUseCase(billRepository, categoryRepository)
    }
}