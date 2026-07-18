package com.example.billtracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [BillEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bill_tracker.db"
                )
                    .addCallback(SeedDefaultCategoriesCallback())
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    private class SeedDefaultCategoriesCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            defaultCategoryEntities.forEach { category ->
                db.execSQL(
                    "INSERT INTO categories (id, name, icon_key, is_custom) VALUES (?, ?, ?, ?)",
                    arrayOf(category.id, category.name, category.iconKey, if (category.isCustom) 1 else 0)
                )
            }
        }
    }
}