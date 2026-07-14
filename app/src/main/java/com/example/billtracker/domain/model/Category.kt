package com.example.billtracker.domain.model


/**
 * @param iconKey string key ไว้ map ไปหา ImageVector ฝั่ง UI
 *        (ดู iconFor() ใน ui/components/CategoryIcon.kt)
 *        เก็บเป็น String เพราะ Room เก็บ ImageVector ตรงๆ ไม่ได้
 * @param isCustom false = default category ที่ผู้ใช้ลบไม่ได้
 */
data class Category(
    val id: Int = 0,
    val name: String,
    val iconKey: String,
    val isCustom: Boolean = true
)

/** Default category 5 อัน ตาม requirement (ลบไม่ได้ในแอปจริง - บังคับ logic นี้ฝั่ง Repository/ViewModel) */
val defaultCategories = listOf(
    Category(id = 1, name = "เช่าบ้าน", iconKey = "home", isCustom = false),
    Category(id = 2, name = "ค่าไฟ", iconKey = "bolt", isCustom = false),
    Category(id = 3, name = "ค่าน้ำ", iconKey = "water_drop", isCustom = false),
    Category(id = 4, name = "อินเทอร์เน็ต", iconKey = "wifi", isCustom = false),
    Category(id = 5, name = "อื่นๆ", iconKey = "more_horiz", isCustom = false),
)
