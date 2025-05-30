package com.LingTH.fridge.Notification

import ExpiryCheckWorker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import java.time.Duration

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ExpiryWorker", "✅ BootReceiver is running...")

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // เรียกใช้หลังจาก boot เสร็จ
            scheduleDailyAlerts(context)
        }
    }

    private fun scheduleDailyAlerts(context: Context) {
        val settingsDao = InventoryDatabase.getDatabase(context).settingsDao()

        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsDao.getSettings() ?: return@launch
            val repeat = settings.repeatAlert.toIntOrNull() ?: 1 // สมมุติว่า repeatAlert คือ "1" ถึง "8"

            val alertManager = AlertTimeManager(repeat)
            val timeSlots = alertManager.debugTimeSlots() // [09:00, 11:00, ...]

            Log.d("BootReceiver", "📆 TimeSlots: $timeSlots")

            timeSlots.forEach { timeString ->
                val alertTime = LocalTime.parse(timeString)
                scheduleWorkAt(context, alertTime)
            }
        }
    }

    private fun scheduleWorkAt(context: Context, time: LocalTime) {
        val now = LocalDateTime.now()
        val targetDateTime = now.withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)
        val delay = if (targetDateTime.isAfter(now)) {
            Duration.between(now, targetDateTime).toMillis()
        } else {
            // ถ้าเวลานั้นเลยมาแล้ว ให้เลื่อนไปวันพรุ่งนี้
            Duration.between(now, targetDateTime.plusDays(1)).toMillis()
        }

        val request = OneTimeWorkRequestBuilder<ExpiryCheckWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
        Log.d("BootReceiver", "⏰ Work scheduled at $time with delay ${delay / 1000}s")
    }

}