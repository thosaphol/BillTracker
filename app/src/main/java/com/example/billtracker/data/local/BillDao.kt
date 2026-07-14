package com.example.billtracker.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BillDao {

    @Query("SELECT * FROM bills ORDER BY due_date ASC")
    fun getAllBills(): LiveData<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun getBillById(id: Int): BillEntity?

    @Query("SELECT * FROM bills WHERE category_id = :categoryId")
    suspend fun getBillsByCategoryId(categoryId: Int): List<BillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: BillEntity): Long

    @Update
    suspend fun update(bill: BillEntity)

    @Delete
    suspend fun delete(bill: BillEntity)

    @Query("UPDATE bills SET is_paid = :isPaid WHERE id = :billId")
    suspend fun updatePaidStatus(billId: Int, isPaid: Boolean)

    @Query("DELETE FROM bills")
    suspend fun deleteAll()
}