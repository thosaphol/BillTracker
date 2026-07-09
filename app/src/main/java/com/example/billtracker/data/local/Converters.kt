package com.example.billtracker.data.local

import androidx.room.TypeConverter
import com.example.billtracker.domain.ReminderType

class Converters {
    @TypeConverter
    fun fromReminderType(value: ReminderType): String = value.name

    @TypeConverter
    fun toReminderType(value: String): ReminderType =
        ReminderType.entries.firstOrNull { it.name == value } ?: ReminderType.NONE
}