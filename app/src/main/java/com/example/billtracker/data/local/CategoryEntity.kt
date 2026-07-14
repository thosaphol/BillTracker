package com.example.billtracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity ของ Category
 * iconKey เก็บเป็น String (เช่น "home", "bolt") แล้ว map เป็น ImageVector
 * ฝั่ง Compose ผ่าน iconFor() ใน ui/components/CategoryIcon.kt
 */
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

/**
 * ข้อมูลตั้งต้นที่ต้อง insert ตอนสร้าง database ครั้งแรก (เช่นใน RoomDatabase.Callback
 * หรือ Repository.seedDefaultCategoriesIfEmpty()) - id ล็อกไว้ 1-5 ไม่ให้ชนกับ custom
 * category ที่ผู้ใช้เพิ่มทีหลัง (autoGenerate จะเริ่มจาก 6 เป็นต้นไปถ้า insert อันนี้ก่อน)
 */
val defaultCategoryEntities = listOf(
    CategoryEntity(id = 1, name = "เช่าบ้าน", iconKey = "home", isCustom = false),
    CategoryEntity(id = 2, name = "ค่าไฟ", iconKey = "bolt", isCustom = false),
    CategoryEntity(id = 3, name = "ค่าน้ำ", iconKey = "water_drop", isCustom = false),
    CategoryEntity(id = 4, name = "อินเทอร์เน็ต", iconKey = "wifi", isCustom = false),
    CategoryEntity(id = 5, name = "อื่นๆ", iconKey = "more_horiz", isCustom = false),
)
