package com.example.billtracker.data.local

import androidx.room.TypeConverter
import com.example.billtracker.domain.model.ReminderType


class Converters {

    @TypeConverter
    fun fromReminderType(value: ReminderType): String = value.name

    @TypeConverter
    fun toReminderType(value: String): ReminderType =
        runCatching { ReminderType.valueOf(value) }.getOrDefault(ReminderType.NONE)
}