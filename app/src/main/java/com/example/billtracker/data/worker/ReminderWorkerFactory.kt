package com.example.billtracker.data.worker

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.billtracker.domain.repository.BillRepository


class ReminderWorkerFactory(
    private val billRepository: BillRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ) = when (workerClassName) {
        ReminderWorker::class.java.name ->
            ReminderWorker(appContext, workerParameters, billRepository)
        else -> null
    }
}