package com.example.myapplication.sortandfilter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SandFscreen(navController: NavHostController) {
    var visibleSelector by remember { mutableStateOf(VisibleSelector.NONE) }
    var selectedCategory by remember { mutableStateOf(emptyList<String>()) }
    var selectedExpiredIn by remember { mutableStateOf(emptyList<String>()) }
    var selectedAdded by remember { mutableStateOf(emptyList<String>()) }
    var selectedAddedPhoto by remember { mutableStateOf(emptyList<String>()) }
    var selectedExpirationDate by remember { mutableStateOf(emptyList<String>()) }
    var Productname by remember { mutableStateOf("") }

    val selectCategory =  selectedCategory.joinToString ( ", " )
    val selectExpiredIn = selectedExpiredIn.joinToString ( ", " )
    val selectAdded = selectedAdded.joinToString ( ", " )
    val selectAddedPhoto = selectedAddedPhoto.joinToString ( ", " )
    val selectExpirationDate = selectedExpirationDate.joinToString ( ", " )



    val CategoryOptions = listOf("Dairy", "Meat", "Vegetable", "fruit", "Frozen", "Still Water", "Soda")
    val ExpiredInOptions = listOf("Expired in 1 day", "Expired in 2 days", "Expired in 3 days", "4day", "5day", "1 week", "2 week", "3 week", "4 week", "1 month", "2 month", "3 month", "6 month")
    val AddedOptions = listOf("Expired in 1 day", "Expired in 2 days", "Expired in 3 days", "4day", "5day", "1 week", "2 week", "3 week", "4 week", "1 month", "2 month", "3 month", "6 month")
    val AddedPhotoOptions = listOf("Added Photo ", "NO Photo ")
    val ExpirationDateOptions = listOf("Ascending", "Descending")
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
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.CATEGORY) {
                OptionSelector("Category:", CategoryOptions, selectedCategory) { option ->
                    selectedCategory = if (option in selectedCategory) selectedCategory - option else selectedCategory + option
                }
            }

            // Alert Mode และ Select Alert Mode
            SettingsItem("Expired in:", selectExpiredIn) {
                visibleSelector = if (visibleSelector == VisibleSelector.Expired_in) VisibleSelector.NONE else VisibleSelector.Expired_in
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.Expired_in) {
                SingleOptionSelector("Select Alert Mode:", ExpiredInOptions, selectedExpiredIn.firstOrNull() ?: "") {
                    selectedExpiredIn = listOf(it)
                }
            }

            // Repeat Alert (time) และ Select Repeat Alert (time)
            SettingsItem("Added:", selectAdded) {
                visibleSelector = if (visibleSelector == VisibleSelector.Added) VisibleSelector.NONE else VisibleSelector.Added
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.Added) {
                SingleOptionSelector("Select Repeat Alert (time):", AddedOptions, selectedAdded.firstOrNull() ?: "") {
                    selectedAdded = listOf(it)
                }
            }
            // Added Photo
            SettingsItem("Added Photo:", selectAddedPhoto) {
                visibleSelector = if (visibleSelector == VisibleSelector.Added_photo) VisibleSelector.NONE else VisibleSelector.Added_photo
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.Added_photo) {
                SingleOptionSelector("Select Added Photo:", AddedPhotoOptions, selectedAddedPhoto.firstOrNull() ?: "") {
                    selectedAddedPhoto = listOf(it)
                }
            }




            Spacer(modifier = Modifier.height(32.dp))
            androidx.compose.material3.Text(
                "Sort by:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            // Expiration Date
            SettingsItem("Expiration Date:", selectExpirationDate) {
                visibleSelector = if (visibleSelector == VisibleSelector.Expiration_Date) VisibleSelector.NONE else VisibleSelector.Expiration_Date
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.Expiration_Date) {
                SingleOptionSelector("Select Expiration Date:", ExpirationDateOptions, selectedExpirationDate.firstOrNull() ?: "") {
                    selectedExpirationDate = listOf(it)
                }
            }

// Product Name
            ProductNameSettingItem(
                label = "Product Name:",
                productName = Productname,
                onProductNameChange = { Productname = it },
                isVisible = visibleSelector == VisibleSelector.Product_Name,
                onToggleVisible = {
                    visibleSelector = if (visibleSelector == VisibleSelector.Product_Name) {
                        VisibleSelector.NONE
                    } else {
                        VisibleSelector.Product_Name
                    }
                }
            )


    }
}
}



