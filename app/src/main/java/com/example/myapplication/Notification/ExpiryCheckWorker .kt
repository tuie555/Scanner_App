import Databases.ProductData
import Databases.daysUntilExpiry
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first

class ExpiryCheckWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @SuppressLint("NewApi")
    override suspend fun doWork(): Result {
        val dao = InventoryDatabase.getDatabase(applicationContext).productDao()

        return try {
            val products = dao.getAllProducts().first() // ใช้ first() เพื่อเก็บ list ครั้งเดียว
            for (product in products) {
                val daysUntilExpiry = product.daysUntilExpiry()
                if (daysUntilExpiry != null && daysUntilExpiry <= 1) {
                    Log.e("ExpiryCheckWorker", "go?")
                    showFoodExpiryNotification(applicationContext, product)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ExpiryCheckWorker", "Error checking expiry", e)
            Result.failure()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showFoodExpiryNotification(context: Context, product: ProductData) {
        Log.d("NOTIFY", "Calling notification for ${product.product_name}")

        val daysUntilExpiry = product.daysUntilExpiry()
        Log.d("NOTIFY", "Checking product: ${product.product_name}, Days left: $daysUntilExpiry")
        val notificationText = if (daysUntilExpiry != null && daysUntilExpiry <= 0) {
            "Product: \"${product.product_name}\" has expired!"
        } else {
            "Product: \"${product.product_name}\" will expire in $daysUntilExpiry day(s)"

        }
        Log.e("ExpiryCheckWorker", "no?")
        val channelId = "expiry_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Expiration Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
        Log.e("ExpiryCheckWorker", "what?")
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Product Expiry Alert")
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(product.id, builder.build())
        Log.e("ExpiryCheckWorker", "what what?")
    }
}

