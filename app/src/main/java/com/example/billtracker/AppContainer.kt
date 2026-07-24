package com.example.billtracker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.billtracker.data.export.JsonDataExporter
import com.example.billtracker.data.local.AppDatabase
import com.example.billtracker.data.remote.BotHolidayApi
import com.example.billtracker.data.repository.BillRepositoryImpl
import com.example.billtracker.data.repository.CategoryRepositoryImpl
import com.example.billtracker.data.repository.HolidayRepositoryImpl
import com.example.billtracker.data.worker.ReminderWorker
import com.example.billtracker.data.worker.ReminderWorkerFactory
import com.example.billtracker.domain.repository.BillRepository
import com.example.billtracker.domain.repository.CategoryRepository
import com.example.billtracker.domain.repository.DataExporter
import com.example.billtracker.domain.repository.HolidayRepository
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
import com.example.billtracker.domain.usecase.holiday.GetHolidaysUseCase
import com.example.billtracker.domain.usecase.setting.DeleteAllDataUseCase
import com.example.billtracker.domain.usecase.setting.ExportDataUseCase
import com.example.billtracker.domain.usecase.setting.ImportDataUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AppContainer(private val context: Context) {

    // ---------- Data layer ----------
    private val database: AppDatabase by lazy { AppDatabase.getInstance(context) }

    private val billRepository: BillRepository by lazy { BillRepositoryImpl(database.billDao()) }
    private val categoryRepository: CategoryRepository by lazy { CategoryRepositoryImpl(database.categoryDao()) }
    private val dataExporter: DataExporter by lazy { JsonDataExporter(context) }


    suspend fun debugTestReminderNow(context: Context) {
        com.example.billtracker.data.worker.ReminderInvoke(context, context, billRepository).invoke()
    }

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
    val deleteCategoryUseCase: DeleteCategoryUseCase by lazy {
        DeleteCategoryUseCase(categoryRepository, billRepository)
    }

    // ---------- Settings use cases ----------
    val exportDataUseCase: ExportDataUseCase by lazy {
        ExportDataUseCase(billRepository, categoryRepository, dataExporter)
    }
    val importDataUseCase: ImportDataUseCase by lazy {
        ImportDataUseCase(billRepository, categoryRepository, dataExporter)
    }
    val deleteAllDataUseCase: DeleteAllDataUseCase by lazy {
        DeleteAllDataUseCase(billRepository, categoryRepository)
    }

    // ---------- Holiday check (BOT API - ธนาคารแห่งประเทศไทย) ----------

    private val botApiToken: String = BuildConfig.BOT_API_TOKEN

    private val botHolidayApi: BotHolidayApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://gateway.api.bot.or.th/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BotHolidayApi::class.java)
    }
    private val holidayRepository: HolidayRepository by lazy {
        HolidayRepositoryImpl(botHolidayApi, botApiToken, context)
    }
    val getHolidaysUseCase: GetHolidaysUseCase by lazy {
        GetHolidaysUseCase(holidayRepository)
    }

    // ---------- WorkManager (reminder notification) ----------

    val workerFactory: ReminderWorkerFactory by lazy { ReminderWorkerFactory(billRepository) }

    fun scheduleReminderWork() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(millisUntilNextQuarterHour(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun millisUntilNextQuarterHour(): Long {
        val now = Calendar.getInstance()
        val next = now.clone() as Calendar
        val currentMinute = now.get(Calendar.MINUTE)
        val minutesToAdd = 15 - (currentMinute % 15)
        next.add(Calendar.MINUTE, minutesToAdd)
        next.set(Calendar.SECOND, 0)
        next.set(Calendar.MILLISECOND, 0)
        return (next.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
    }
}
