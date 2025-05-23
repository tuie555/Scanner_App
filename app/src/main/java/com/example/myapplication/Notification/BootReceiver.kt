package com.example.myapplication.notification import android.content.BroadcastReceiver

import ExpiryCheckWorker
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.Notification.NotificationTest

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "BOOT_COMPLETED received")

            val workRequest = OneTimeWorkRequestBuilder<ExpiryCheckWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}







