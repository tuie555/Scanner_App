package Databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: ProductData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: InspectionData)

    @Query("SELECT * FROM product_table")
    fun getAllProducts(): Flow<List<ProductData>>

    @Query("SELECT * FROM inspection_table WHERE product_id = :productId")
    fun getInspectionsByProductId(productId: Int): Flow<List<InspectionData>>

    @Query("SELECT COUNT(*) FROM inspection_table")
    fun getInspectionCount(): Int

    @Query("SELECT * FROM product_table")
    suspend fun getAllProductsOnce(): List<ProductData>

    @Query("SELECT * FROM product_table WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): ProductData?

    @Query("SELECT DISTINCT categories FROM product_table")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM product_table WHERE image_url IS NOT NULL")
    fun getWithPhotos(): Flow<List<ProductData>>

    @Query("SELECT * FROM product_table WHERE image_url IS NULL")
    fun getWithoutPhotos(): Flow<List<ProductData>>

    // ดึงวันหมดอายุทั้งหมด (ใช้ในกรณีทำ ExpiredIn ได้เอง)
    @Query("SELECT expiration_date FROM product_table")
    fun getAllExpirationDates(): Flow<List<Long>>
    @Query("SELECT add_day FROM product_table")
    fun getAllAddedDates(): Flow<List<Long>>
    @Update
    suspend fun updateProduct(product: ProductData)

}


