package com.example.billtracker

import android.app.Application
import androidx.work.Configuration
import com.example.billtracker.data.notification.BillNotificationHelper

class BillTrackerApplication : Application(), Configuration.Provider {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)

        BillNotificationHelper.createNotificationChannel(this)
        container.scheduleReminderWork()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(container.workerFactory)
            .build()
}