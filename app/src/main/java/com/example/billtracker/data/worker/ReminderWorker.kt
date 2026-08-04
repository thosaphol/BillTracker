package com.example.billtracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.billtracker.data.notification.BillNotificationHelper
import com.example.billtracker.data.notification.ReminderTracker
import com.example.billtracker.domain.model.Bill
import com.example.billtracker.domain.model.ReminderType
import com.example.billtracker.domain.model.effectiveReminderStart
import com.example.billtracker.domain.repository.BillRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val billRepository: BillRepository
) : CoroutineWorker(context, params) {
    private val reminderInvoke = ReminderInvoke(context,applicationContext,billRepository)

    override suspend fun doWork(): Result {
        return reminderInvoke.invoke()
    }

    companion object {
        const val WORK_NAME = "bill_reminder_periodic_work"
    }


}

class ReminderInvoke(context: Context,
                     val applicationContext:Context,
                     private val billRepository: BillRepository){
    private val tracker = ReminderTracker(context)

    public suspend fun invoke(): ListenableWorker.Result{
        val now = Calendar.getInstance()
        val slot = currentSlot(now) ?: return ListenableWorker.Result.success()

        val unpaidBills = billRepository.getAllBillsOnce().filter { !it.isPaid }

        unpaidBills.forEach { bill ->
            if (shouldNotify(bill, slot, now) && !tracker.hasNotified(bill.id, slot)) {
                BillNotificationHelper.showReminderNotification(applicationContext, bill)
                tracker.markNotified(bill.id, slot)
            }
        }

        return ListenableWorker.Result.success()
    }



    private fun currentSlot(now: Calendar): String? {
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val minute = now.get(Calendar.MINUTE)
        if (minute >= 15) return null // ให้โอกาสแค่ 15 นาทีแรกของชั่วโมงนั้น กันเตือนข้ามชั่วโมงผิด
        return when (hour) {
            9 -> "09:00"
            13 -> "13:00"
            20 -> "20:00"
            else -> null
        }
    }

    private fun shouldNotify(bill: Bill, slot: String, now: Calendar): Boolean {
        return when (bill.reminderType) {
            ReminderType.NONE -> false

            ReminderType.DAILY ->
                (slot == "20:00" || slot == "09:00" || slot == "13:00" ) && !isBeforeStartDate(bill, now)

            ReminderType.MONTHLY -> {
                if (isBeforeStartDate(bill, now)) return false
                val daysSinceStart = daysBetween(bill.effectiveReminderStart(), now.timeInMillis)
                val isOverdue = isSameOrAfterDay(now.timeInMillis, bill.dueDate)

                when {
                    daysSinceStart in 0..1 -> true
                    isOverdue -> slot == "09:00"
                    else -> false
                }
            }
        }
    }

    private fun isBeforeStartDate(bill: Bill, now: Calendar): Boolean {
        val isBeforeStartDate = startOfDay(now.timeInMillis) < startOfDay(bill.effectiveReminderStart())
        return isBeforeStartDate
    }


    private fun daysBetween(startMillis: Long, endMillis: Long): Long {
        val start = startOfDay(startMillis)
        val end = startOfDay(endMillis)
        return (end - start) / (24 * 60 * 60 * 1000L)
    }

    private fun isSameOrAfterDay(millisA: Long, millisB: Long): Boolean =
        startOfDay(millisA) >= startOfDay(millisB)

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}