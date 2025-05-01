package com.example.myapplication.data
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "product_table")
data class ProductData(
    @PrimaryKey val barcode: String,
    val product_name: String,
    val categories: String,
    val image_url: String
)


