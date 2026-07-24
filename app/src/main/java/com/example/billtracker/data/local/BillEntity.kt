package com.example.billtracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.billtracker.domain.model.ReminderType

@Entity(tableName = "bills")
@TypeConverters(Converters::class)
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val amount: Double,

    @ColumnInfo(name = "due_date")
    val dueDate: Long,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    @ColumnInfo(name = "is_paid")
    val isPaid: Boolean = false,

    val note: String = "",

    @ColumnInfo(name = "reminder_type")
    val reminderType: ReminderType = ReminderType.NONE,

    @ColumnInfo(name = "reminder_start_date")
    val reminderStartDate: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
