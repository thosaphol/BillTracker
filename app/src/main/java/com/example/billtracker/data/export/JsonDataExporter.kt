package com.example.billtracker.data.export

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.Category
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.domain.repository.DataExporter
import com.example.billtracker.domain.repository.ImportedData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class JsonDataExporter @Inject constructor(
    @param:ApplicationContext private val context: Context
) : DataExporter {

    override suspend fun export(bills: List<Bill>, categories: List<Category>, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val json = buildJson(bills, categories)
                val encrypted = EncryptionHelper.encrypt(json.toString(), password)
                val fileName = "bill_tracker_export_${System.currentTimeMillis()}.enc"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    exportViaMediaStore(encrypted, fileName)
                } else {
                    exportViaLegacyFile(encrypted, fileName)
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun exportViaMediaStore(content: String, fileName: String) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/BillTracker")
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = context.contentResolver.insert(collection, values)
            ?: throw IllegalStateException("สร้างไฟล์ผ่าน MediaStore ไม่สำเร็จ")

        context.contentResolver.openOutputStream(uri)?.use { stream ->
            stream.write(content.toByteArray())
        } ?: throw IllegalStateException("เปิด output stream ไม่สำเร็จ")
    }


    private fun exportViaLegacyFile(content: String, fileName: String) {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val appDir = File(downloadDir, "BillTracker").apply { mkdirs() }
        val file = File(appDir, fileName)
        file.writeText(content)
    }


    override suspend fun import(uri: Uri, password: String): Result<ImportedData> =
        withContext(Dispatchers.IO) {
            try {
                val encryptedContent = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader().readText()
                } ?: return@withContext Result.failure(IllegalStateException("เปิดไฟล์ไม่สำเร็จ"))

                val decrypted = try {
                    EncryptionHelper.decrypt(encryptedContent, password)
                } catch (e: Exception) {
                    // ถอดรหัสไม่ผ่าน = รหัสผ่านผิด หรือไฟล์เสีย/ไม่ใช่ไฟล์ที่แอปนี้ export ออกมา
                    return@withContext Result.failure(IllegalArgumentException("รหัสผ่านไม่ถูกต้อง หรือไฟล์เสียหาย"))
                }

                val json = JSONObject(decrypted)
                val bills = parseBills(json.getJSONArray("bills"))
                val categories = parseCategories(json.getJSONArray("categories"))

                Result.success(ImportedData(bills, categories))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun parseBills(array: JSONArray): List<Bill> =
        (0 until array.length()).map { i -> array.getJSONObject(i).toBill() }

    private fun parseCategories(array: JSONArray): List<Category> =
        (0 until array.length()).map { i -> array.getJSONObject(i).toCategory() }

    private fun JSONObject.toBill(): Bill = Bill(
        id = getInt("id"),
        title = getString("title"),
        amount = getDouble("amount"),
        dueDate = getLong("dueDate"),
        categoryId = getInt("categoryId"),
        isPaid = getBoolean("isPaid"),
        note = getString("note"),
        reminderType = runCatching { ReminderType.valueOf(getString("reminderType")) }
            .getOrDefault(ReminderType.NONE),
        reminderStartDate = if (isNull("reminderStartDate")) null else getLong("reminderStartDate"),
        createdAt = getLong("createdAt")
    )

    private fun JSONObject.toCategory(): Category = Category(
        id = getInt("id"),
        name = getString("name"),
        iconKey = getString("iconKey"),
        isCustom = getBoolean("isCustom")
    )

    private fun buildJson(bills: List<Bill>, categories: List<Category>): JSONObject =
        JSONObject().apply {
            put("exportedAt", System.currentTimeMillis())
            put("bills", JSONArray(bills.map { it.toJson() }))
            put("categories", JSONArray(categories.map { it.toJson() }))
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