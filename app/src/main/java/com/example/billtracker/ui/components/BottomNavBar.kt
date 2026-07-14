package com.example.billtracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


/**
 * เมนูล่างของแอป มีแค่ 3 หน้าที่ตรงกับ requirement จริง
 * (ตัด Charts/Summary ออกจาก mockup เพราะไม่มีฟีเจอร์นี้ในสโคป)
 */
enum class BottomNavDestination(val label: String, val icon: ImageVector) {
    BILLS("รายการ", Icons.Default.Receipt),
    CATEGORIES("หมวดหมู่", Icons.Default.Category),
    SETTINGS("ตั้งค่า", Icons.Default.Settings),
}

@Composable
fun BottomNavBar(
    current: BottomNavDestination,
    onSelect: (BottomNavDestination) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        BottomNavDestination.entries.forEach { dest ->
            NavigationBarItem(
                selected = dest == current,
                onClick = { onSelect(dest) },
                icon = { Icon(dest.icon, contentDescription = dest.label) },
                label = { Text(dest.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                )
            )
        }
    }
}