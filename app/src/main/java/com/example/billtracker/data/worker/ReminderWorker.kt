package com.example.billtracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.billtracker.data.notification.BillNotificationHelper
import com.example.billtracker.data.notification.ReminderTracker
import com.example.billtracker.domain.reminder.ReminderScheduler
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
    private val reminderInvoke = ReminderInvoke(context, applicationContext, billRepository)

    override suspend fun doWork(): Result {
        return reminderInvoke.invoke()
    }

    companion object {
        const val WORK_NAME = "bill_reminder_periodic_work"
    }
}

class ReminderInvoke(
    context: Context,
    val applicationContext: Context,
    private val billRepository: BillRepository
) {
    private val tracker = ReminderTracker(context)

    suspend fun invoke(): ListenableWorker.Result {
        val now = Calendar.getInstance()
        val slot = ReminderScheduler.currentSlot(now) ?: return ListenableWorker.Result.success()

        val unpaidBills = billRepository.getAllBillsOnce().filter { !it.isPaid }

        unpaidBills.forEach { bill ->
            if (ReminderScheduler.shouldNotify(bill, slot, now) && !tracker.hasNotified(bill.id, slot)) {
                BillNotificationHelper.showReminderNotification(applicationContext, bill)
                tracker.markNotified(bill.id, slot)
            }
        }

        return ListenableWorker.Result.success()
    }
}