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


    @Update
    suspend fun updateProduct(product: ProductData)

}


