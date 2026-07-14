package com.example.billtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.billtracker.ui.navigation.BillTrackerNavHost
import com.example.billtracker.ui.theme.BillTrackerTheme
// import dagger.hilt.android.AndroidEntryPoint  // <- เปิดใช้ตอนใส่ Hilt จริง

// @AndroidEntryPoint  // <- ต้องมี annotation นี้ถ้าจะใช้ hiltViewModel() ในหน้าจอลูก
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ดึง AppContainer จาก Application (manual DI - ไม่ใช้ Hilt)
        val container = (application as BillTrackerApplication).container

        setContent {
            BillTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BillTrackerNavHost(container = container)
                }
            }
        }
    }
}