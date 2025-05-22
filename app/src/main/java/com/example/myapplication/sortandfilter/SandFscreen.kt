package com.example.myapplication.sortandfilter

import Databases.ProductDao
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.myapplication.setting.components.OptionSelector
import com.example.myapplication.setting.components.SettingsItem
import com.example.myapplication.setting.components.SingleOptionSelector

enum class VisibleSelector {
    NONE,
    CATEGORY,
    Expired_in,
    Added,
    Added_photo,
    Expiration_Date,
    Product_Name,
}
@Composable
fun SandFscreen(navController: NavHostController,dao: ProductDao) {

    val viewModel: FilterViewModel = viewModel(
        factory = FilterViewModelFactory(dao)
    )


    var visibleSelector by remember { mutableStateOf(VisibleSelector.NONE) }
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedExpiredIn by viewModel.selectedExpiredIn.collectAsState()
    val selectedAdded by viewModel.selectedAdded.collectAsState()
    val selectedAddedPhoto by viewModel.selectedAddedPhoto.collectAsState()
    val selectedExpirationDate by viewModel.selectedExpirationDate.collectAsState()
    val productName by viewModel.productName.collectAsState()

    val selectCategory =  selectedCategory.joinToString ( ", " )
    val selectExpiredIn = selectedExpiredIn.joinToString ( ", " )
    val selectAdded = selectedAdded.joinToString ( ", " )
    val selectAddedPhoto = selectedAddedPhoto.joinToString ( ", " )
    val selectExpirationDate = selectedExpirationDate.joinToString ( ", " )



    val categories by viewModel.allCategories.collectAsState()
    val productsWithPhoto by viewModel.allProductsWithPhotos.collectAsState()
    val productsWithoutPhoto by viewModel.allProductsWithoutPhotos.collectAsState()
    val expirationDates by viewModel.allExpirationDates.collectAsState()
    val addedDates by viewModel.allAddedDates.collectAsState()
    val AddedPhotoOptions = listOfNotNull(
        if (productsWithPhoto.isNotEmpty()) "Added Photo" else null,
        if (productsWithoutPhoto.isNotEmpty()) "NO Photo" else null
    )

    val ExpiredInOptions = expirationDates.map { date ->
        // ประมวลผลวันที่ให้เป็น "เหลืออีก ... วัน"
        val daysLeft = (date - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
        "Expired in $daysLeft day(s)"
    }
    val AddedOptions = addedDates.map { date ->
        val daysAgo = ((System.currentTimeMillis() - date) / (1000 * 60 * 60 * 24)).toInt()
        "Added $daysAgo day(s) ago"
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            androidx.compose.material3.Text(
                "Filter by:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Alert before expired และ Select Alert before expired
            SettingsItem("Category:", selectCategory) {
                visibleSelector = if (visibleSelector == VisibleSelector.CATEGORY) VisibleSelector.NONE else VisibleSelector.CATEGORY
            }
            AnimatedVisibility(visibleSelector == VisibleSelector.CATEGORY) {
                OptionSelector("Category:", categories, selectedCategory) { option ->
                    val newSelection = if (option in selectedCategory) selectedCategory - option else selectedCategory + option
                    viewModel.setSelectedCategory(newSelection)
                }
            }

            SettingsItem("Expired in:", selectExpiredIn) {
                visibleSelector = if (visibleSelector == VisibleSelector.Expired_in) VisibleSelector.NONE else VisibleSelector.Expired_in
            }
            AnimatedVisibility(visibleSelector == VisibleSelector.Expired_in) {
                SingleOptionSelector("Select Alert Mode:", ExpiredInOptions, selectedExpiredIn.firstOrNull() ?: "") {
                    viewModel.setSelectedExpiredIn(listOf(it))
                }
            }

            SettingsItem("Added:", selectAdded) {
                visibleSelector = if (visibleSelector == VisibleSelector.Added) VisibleSelector.NONE else VisibleSelector.Added
            }
            AnimatedVisibility(visibleSelector == VisibleSelector.Added) {
                SingleOptionSelector("Select Repeat Alert (time):", AddedOptions, selectedAdded.firstOrNull() ?: "") {
                    viewModel.setSelectedAdded(listOf(it))
                }
            }

            SettingsItem("Added Photo:", selectAddedPhoto) {
                visibleSelector = if (visibleSelector == VisibleSelector.Added_photo) VisibleSelector.NONE else VisibleSelector.Added_photo
            }
            AnimatedVisibility(visibleSelector == VisibleSelector.Added_photo) {
                SingleOptionSelector("Select Added Photo:", AddedPhotoOptions, selectedAddedPhoto.firstOrNull() ?: "") {
                    viewModel.setSelectedAddedPhoto(listOf(it))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Sort by:", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            SettingsItem("Expiration Date:", selectExpirationDate) {
                visibleSelector = if (visibleSelector == VisibleSelector.Expiration_Date) VisibleSelector.NONE else VisibleSelector.Expiration_Date
            }
            AnimatedVisibility(visibleSelector == VisibleSelector.Expiration_Date) {
                SingleOptionSelector("Select Expiration Date:", ExpiredInOptions, selectedExpirationDate.firstOrNull() ?: "") {
                    viewModel.setSelectedExpirationDate(listOf(it))
                }
            }

            LaunchedEffect(categories) {
                println("📦 All Categories:")
                val rawString = "ข้อความภาษาไทย: น้ำอัดลม, ขนม, โคล่า"
                val decoded = java.net.URLDecoder.decode(rawString, "UTF-8")
                Log.d("TEST", decoded)


                categories.forEach { println("  • $it") }
            }

            AnimatedVisibility(visibleSelector == VisibleSelector.CATEGORY) {
                OptionSelector("Category:", categories, selectedCategory) { option ->
                    val newSelection = if (option in selectedCategory) {
                        selectedCategory - option
                    } else {
                        selectedCategory + option
                    }

                    // 🔽 DEBUG: แสดงการคลิกเลือก
                    println("🔘 Clicked: $option")
                    println("📌 New Selection: $newSelection")

                    viewModel.setSelectedCategory(newSelection)
                }
        }
    }

    }
}




