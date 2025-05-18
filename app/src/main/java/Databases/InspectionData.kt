package Databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "inspection_table",
    foreignKeys = [ForeignKey(
        entity = ProductData::class,
        parentColumns = ["id"],            // อ้างอิง Primary Key ของ Databases.ProductData
        childColumns = ["product_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class InspectionData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "product_id") val productId: Int,       // foreign key เป็น Int
    @ColumnInfo(name = "inspection_date") val inspectionDate: Long,
    @ColumnInfo(name = "inspection_notes") val inspectionNotes: String?
)
