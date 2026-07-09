package com.example.billtracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    // เก็บเป็น String map ไปหา ImageVector ผ่าน iconFor() ใน CategoryIcon.kt
    @ColumnInfo(name = "icon_key")
    val iconKey: String,

    @ColumnInfo(name = "is_custom")
    val isCustom: Boolean = true
)
