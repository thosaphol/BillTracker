package com.example.billtracker.domain.model



data class Category(
    val id: Int = 0,
    val name: String,
    val iconKey: String,
    val isCustom: Boolean = true
)


val defaultCategories = listOf(
    Category(id = 1, name = "เช่าบ้าน", iconKey = "home", isCustom = false),
    Category(id = 2, name = "ค่าไฟ", iconKey = "bolt", isCustom = false),
    Category(id = 3, name = "ค่าน้ำ", iconKey = "water_drop", isCustom = false),
    Category(id = 4, name = "อินเทอร์เน็ต", iconKey = "wifi", isCustom = false),
    Category(id = 5, name = "อื่นๆ", iconKey = "more_horiz", isCustom = false),
)
