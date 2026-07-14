package com.example.billtracker.data.export

import android.content.Context
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.repository.DataExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Export เป็นไฟล์ JSON เก็บที่ external-files-dir ของแอปเอง
 * (context.getExternalFilesDir ไม่ต้องขอ permission WRITE_EXTERNAL_STORAGE
 * บน Android 10+ เพราะเป็น scoped storage ของแอปเอง - สอดคล้องกับ PDPA
 * ที่ตกลงไว้ว่าไม่ขอ permission เกินจำเป็น)
 *
 * ใช้ org.json ที่มากับ Android SDK อยู่แล้ว ไม่ต้องเพิ่ม dependency (เช่น Gson) เพิ่ม
 */
class JsonDataExporter(
    private val context: Context
) : DataExporter {

    override suspend fun export(bills: List<Bill>, categories: List<Category>): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("exportedAt", System.currentTimeMillis())
                    put("bills", JSONArray(bills.map { it.toJson() }))
                    put("categories", JSONArray(categories.map { it.toJson() }))
                }

                val exportDir = File(context.getExternalFilesDir(null), "exports").apply { mkdirs() }
                val file = File(exportDir, "bill_tracker_export_${System.currentTimeMillis()}.json")
                file.writeText(json.toString(2))

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun Bill.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("amount", amount)
        put("dueDate", dueDate)
        put("categoryId", categoryId)
        put("isPaid", isPaid)
        put("note", note)
        put("reminderType", reminderType.name)
        put("reminderStartDate", reminderStartDate ?: JSONObject.NULL)
        put("createdAt", createdAt)
    }

    private fun Category.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("iconKey", iconKey)
        put("isCustom", isCustom)
    }
}