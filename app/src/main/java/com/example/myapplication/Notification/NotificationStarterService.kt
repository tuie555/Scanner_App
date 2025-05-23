package com.example.myapplication.Notification import Databases.ProductData
import Databases.daysUntilExpiry
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import kotlinx.coroutines.runBlocking

class NotificationStarterService : IntentService("NotificationStarterService") {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHandleIntent(intent: Intent?) {
        val productDao = InventoryDatabase.getDatabase(this).productDao()

        runBlocking {
            val productList = productDao.getAllProductsOnce() // ต้องเขียนเพิ่มใน ProductDao
            productList.forEach { product ->
                val days = product.daysUntilExpiry()
                if (days != null && days <= 1) {
                    showNotification(product)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(product: ProductData) {
        val channelId = "expiry_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, "Expiration Alerts", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val days = product.daysUntilExpiry()
        val text = if (days != null && days <= 0) {
            "Product: \"${product.product_name}\" has expired!"
        } else {
            "Product: \"${product.product_name}\" will expire in $days day(s)"
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Expiry Notification")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(product.id, notification)
    }
}
