package com.example.billtracker.common

/**
 * Wrapper สำหรับ one-time event ผ่าน LiveData (เช่น "แสดง Snackbar ครั้งเดียว",
 * "นำทางกลับ") ป้องกันปัญหา LiveData ปกติที่ยิง event ซ้ำตอนหมุนจอ/recompose
 * เพราะ Activity สร้างใหม่แล้ว observe ค่าเก่าซ้ำ
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    /** เรียกครั้งแรกได้ค่า, เรียกซ้ำได้ null เสมอ */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /** เผื่อกรณีอยากดูค่าโดยไม่ mark ว่า handle แล้ว (ใช้ตอน debug/log เท่านั้น) */
    fun peekContent(): T = content
}