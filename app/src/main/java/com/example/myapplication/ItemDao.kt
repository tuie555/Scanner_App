import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(item: ProductData)

    @Update
    suspend fun update(item: ProductData)

    @Delete
    suspend fun delete(item: ProductData)

    @Query("SELECT * FROM items ORDER BY id DESC")
    suspend fun getAllItems(): List<ProductData>

    @Query("SELECT * FROM items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: Int): ProductData?
}
