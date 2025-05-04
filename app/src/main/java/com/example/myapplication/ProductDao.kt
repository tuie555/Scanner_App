import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.ProductData
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductData)

    @Query("SELECT * FROM product_table")
     fun getAllProducts(): Flow<List<ProductData>>

    @Query("SELECT categories FROM product_table")
    suspend fun getcategories(): List<String>

}
