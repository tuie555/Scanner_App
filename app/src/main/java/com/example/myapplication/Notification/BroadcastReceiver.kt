package com.example.myapplication.Notification

import ExpiryCheckWorker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ExpiryWorker", "✅ BootReceiver is running...")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleExpiryCheck(context)
        }

    }
    fun scheduleExpiryCheck(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<ExpiryCheckWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        Log.d("BootReceiver", "🛠 OneTimeWorkRequest scheduled")
    }

}
