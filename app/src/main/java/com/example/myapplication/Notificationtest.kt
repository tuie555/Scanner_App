package com.example.myapplication;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.myapplication.R

class Notificationtest : AppCompatActivity() {

    private val CHANNEL_ID = "channel_id_example"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // เรียกฟังก์ชันแสดงแจ้งเตือน
        showFoodExpiryNotification("ไข่ไก่")
    }

    fun Context.showFoodExpiryNotification(foodName: String) {
        val channelId = "food_expiry_channel"

        // สร้าง Notification Channel หากยังไม่มี
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Food Expiry Alerts"
            val descriptionText = "Notifications for food expiration"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // สร้าง Notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon) // แนะนำให้ใช้ไอคอนที่เกี่ยวกับอาหาร
            .setContentTitle("แจ้งเตือนอาหารใกล้หมดอายุ")
            .setContentText("รายการ: \"$foodName\" ใกล้หมดอายุแล้ว!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(foodName.hashCode(), builder.build()) // ใช้ hash ของชื่อเพื่อหลีกเลี่ยง ID ซ้ำ
    }

}
