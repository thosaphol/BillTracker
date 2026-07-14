package com.example.billtracker.data.local

import androidx.room.TypeConverter
import com.example.billtracker.domain.model.ReminderType

/**
 * Room เก็บ enum ตรงๆ ไม่ได้ ต้องแปลงเป็น String ก่อน
 * ต้องแนบ @TypeConverters(Converters::class) ไว้ที่ @Database class ด้วย
 */
class Converters {

    @TypeConverter
    fun fromReminderType(value: ReminderType): String = value.name

    @TypeConverter
    fun toReminderType(value: String): ReminderType =
        runCatching { ReminderType.valueOf(value) }.getOrDefault(ReminderType.NONE)
}