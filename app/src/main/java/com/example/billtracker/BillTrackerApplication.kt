package com.example.billtracker

import android.app.Application


/**
 * ไม่ใช้ @HiltAndroidApp - สร้าง AppContainer เองตอนแอปเริ่มทำงาน
 * แล้วให้ Activity/Composable ดึง container ผ่าน (application as BillTrackerApplication).container
 */
class BillTrackerApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}