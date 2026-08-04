package com.example.billtracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.billtracker.data.notification.BillNotificationHelper
import com.example.billtracker.data.worker.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BillTrackerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()

        BillNotificationHelper.createNotificationChannel(this)
        scheduleReminderWork()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    fun scheduleReminderWork() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
            .setInitialDelay(millisUntilNextQuarterHour(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
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