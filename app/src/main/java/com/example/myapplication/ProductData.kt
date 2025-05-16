package com.example.myapplication.data
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "product_table")
data class ProductData(
    @PrimaryKey val barcode: String,
    val product_name: String,
    val categories: String,
    val image_url: String,
    val expiration_date: Long?,
    val add_day: Long?,
    val notes: String


)


