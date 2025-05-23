package com.example.myapplication.Notification import Databases.ProductDao
import Databases.ProductData
import Databases.daysUntilExpiry
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import kotlinx.coroutines.launch

class NotificationTest : AppCompatActivity() {

    private lateinit var productDao: ProductDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
        } catch (e: Exception) {
            Log.e("NotificationTest", "Error in setContentView", e)
        }

        productDao = InventoryDatabase.getDatabase(this).productDao()
        Log.e("NotificationTest", "run")
        checkExpiringProducts() // Check and notify

    }

    /**
     * Check for products that are about to expire
     */
    @SuppressLint("NewApi")
    private fun checkExpiringProducts() {
        lifecycleScope.launch {
            try {
                productDao.getAllProducts().collect { productList ->
                    productList.forEach { product ->
                        val daysUntilExpiry = product.daysUntilExpiry()
                        if (daysUntilExpiry != null && daysUntilExpiry <= 1) {
                            showFoodExpiryNotification(applicationContext, product)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationTest", "Error checking expiry", e)
            }
        }
    }



    /**
     * Show Notification
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showFoodExpiryNotification(context: Context, product: ProductData) {
        Log.d("NOTIFY", "Notify called for ${product.product_name}")
        val daysUntilExpiry = product.daysUntilExpiry()
        val notificationText = if (daysUntilExpiry != null && daysUntilExpiry <= 0) {
            "Product: \"${product.product_name}\" has expired!"
        } else {
            "Product: \"${product.product_name}\" will expire in $daysUntilExpiry day(s)"
        }

        val channelId = "expiry_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Expiration Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Product Expiry Alert")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        Log.d("NOTIFY", "Sending notification: $notificationText")
        notificationManager.notify(product.id, builder.build())
    }


}





