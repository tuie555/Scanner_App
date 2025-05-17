import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.InspectionData
import com.example.myapplication.migration.MIGRATION_1_2


@Database(entities = [ProductData::class, InspectionData::class], version = 2)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDatabase::class.java,
                    "inventory_database"
                )
                    .addMigrations(MIGRATION_1_2)              // ← เพิ่มบรรทัดนี้
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


