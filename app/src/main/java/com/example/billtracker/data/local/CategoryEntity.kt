package com.example.billtracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    @ColumnInfo(name = "icon_key")
    val iconKey: String,

    // default category (เช่าบ้าน, ค่าไฟ, ค่าน้ำ, อินเทอร์เน็ต, อื่นๆ) = false ลบไม่ได้
    // custom category ที่ผู้ใช้เพิ่มเอง = true ลบได้
    @ColumnInfo(name = "is_custom", defaultValue = "1")
    val isCustom: Boolean = true
)

val defaultCategoryEntities = listOf(
    CategoryEntity(id = 1, name = "เช่าบ้าน", iconKey = "home", isCustom = false),
    CategoryEntity(id = 2, name = "ค่าไฟ", iconKey = "bolt", isCustom = false),
    CategoryEntity(id = 3, name = "ค่าน้ำ", iconKey = "water_drop", isCustom = false),
    CategoryEntity(id = 4, name = "อินเทอร์เน็ต", iconKey = "wifi", isCustom = false),
    CategoryEntity(id = 5, name = "อื่นๆ", iconKey = "more_horiz", isCustom = false),
)
