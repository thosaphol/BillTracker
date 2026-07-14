package com.example.billtracker.ui.components

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val thaiMonths = listOf(
    "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
    "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
)

/** แสดงจำนวนเงินแบบ ฿1,500.00 */
fun formatBaht(amount: Double): String {
    val nf = NumberFormat.getNumberInstance(Locale.US)
    nf.minimumFractionDigits = 2
    nf.maximumFractionDigits = 2
    return "฿${nf.format(amount)}"
}

/** แสดงวันที่แบบ 25 ต.ค. 2566 (แบบย่อ ใช้ใน list) */
fun formatDateShort(epochMillis: Long): String {
    val sdf = SimpleDateFormat("d MMM yyyy", Locale.US)
    return sdf.format(Date(epochMillis))
}

/** แสดงวันที่แบบเต็ม 25 ตุลาคม 2566 (พ.ศ.) ใช้ใน detail screen */
fun formatDateFullThaiYear(epochMillis: Long): String {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = epochMillis
    val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
    val month = thaiMonths[cal.get(java.util.Calendar.MONTH)]
    val year = cal.get(java.util.Calendar.YEAR) + 543
    return "$day $month $year"
}