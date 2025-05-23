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
import androidx.compose.runtime.collectAsState
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class VisibleSelector {
    NONE,
    CATEGORY,
    Expired_in,
    Added,
    Added_photo,
    Expiration_Date,
    Product_Name,
}
// SandFscreen.kt
@Composable
fun SandFscreen(
    navController: NavHostController,
    dao: ProductDao,
    filterViewModel: FilterViewModel
) {
    var visibleSelector by remember { mutableStateOf(VisibleSelector.NONE) }

    val selectedCategory by filterViewModel.selectedCategory.collectAsState()
    val selectedExpiredIn by filterViewModel.selectedExpiredIn.collectAsState()
    val selectedAdded by filterViewModel.selectedAdded.collectAsState()
    val selectedAddedPhoto by filterViewModel.selectedAddedPhoto.collectAsState()
    val selectedExpirationDate by filterViewModel.selectedExpirationDate.collectAsState()
    val productName by filterViewModel.productName.collectAsState()

    val selectCategory = selectedCategory.joinToString(", ")
    val selectExpiredIn = selectedExpiredIn.joinToString(", ")
    val selectAdded = selectedAdded.joinToString(", ")
    val selectAddedPhoto = selectedAddedPhoto.joinToString(", ")
    val selectExpirationDate = selectedExpirationDate.joinToString(", ")

    val categories by filterViewModel.allCategories.collectAsState()
    val productsWithPhoto by filterViewModel.allProductsWithPhotos.collectAsState()
    val productsWithoutPhoto by filterViewModel.allProductsWithoutPhotos.collectAsState()
    val expirationDates by filterViewModel.allExpirationDates.collectAsState()
    val addedDates by filterViewModel.allAddedDates.collectAsState()

    val AddedPhotoOptions = listOfNotNull(
        if (productsWithPhoto.isNotEmpty()) "Added Photo" else null,
        if (productsWithoutPhoto.isNotEmpty()) "NO Photo" else null
    )

    val now = System.currentTimeMillis()

    val expiredInOptions = expirationDates
        .map { truncateToMidnight(it) }
        .distinct()
        .filter { it > now }
        .map { date ->
            val daysLeft = ((date - now) / (1000 * 60 * 60 * 24)).toInt()
            date to when {
                daysLeft >= 365 -> "Expired in ${daysLeft / 365} year(s)"
                daysLeft >= 30 -> "Expired in ${daysLeft / 30} month(s)"
                else -> "Expired in $daysLeft day(s)"
            }
        }


    val expiredInValues = expiredInOptions.map { it.first }
    val expiredInLabels = expiredInOptions.map { it.second }

    val expirationDateLabels = expirationDates
        .map { truncateToMidnight(it) }
        .distinct()
        .associateWith { timestamp ->
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        }


    val validAddedDates = addedDates.filter { it > 0 }
    val AddedOptions = validAddedDates
        .map { truncateToMidnight(it) }
        .distinct()
        .filter { it <= now }
        .map { date ->
            val daysAgo = ((now - date) / (1000 * 60 * 60 * 24)).toInt()
            when {
                daysAgo >= 365 -> "Added ${daysAgo / 365} year(s) ago"
                daysAgo >= 30 -> "Added ${daysAgo / 30} month(s) ago"
                else -> "Added $daysAgo day(s) ago"
            }
        }


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Filter by:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Category
        SettingsItem("Category:", selectCategory) {
            visibleSelector = toggleSelector(visibleSelector, VisibleSelector.CATEGORY)
        }
        AnimatedVisibility(visibleSelector == VisibleSelector.CATEGORY) {
            OptionSelector("Category:", categories, selectedCategory) { option ->
                val updated = if (option in selectedCategory) selectedCategory - option else selectedCategory + option
                filterViewModel.setSelectedCategory(updated)
            }
        }

        // Expired In
        SettingsItem("Expired in:", selectedExpiredIn.firstOrNull()?.let { expiredInLabels.getOrNull(expiredInValues.indexOf(it)) } ?: "") {
            visibleSelector = toggleSelector(visibleSelector, VisibleSelector.Expired_in)
        }
        AnimatedVisibility(visibleSelector == VisibleSelector.Expired_in) {
            SingleOptionSelector(
                title = "Select Expiration Range:",
                options = expiredInLabels,
                selectedOption = selectedExpiredIn.firstOrNull()?.let { expiredInLabels.getOrNull(expiredInValues.indexOf(it)) } ?: "",
                onOptionToggle = { selectedLabel ->
                    val index = expiredInLabels.indexOf(selectedLabel)
                    val selected = if (index >= 0) listOf(expiredInValues[index]) else emptyList()
                    filterViewModel.setSelectedExpiredIn(selected)
                }
            )
        }

        // Added
        SettingsItem("Added:", selectAdded) {
            visibleSelector = toggleSelector(visibleSelector, VisibleSelector.Added)
        }
        AnimatedVisibility(visibleSelector == VisibleSelector.Added) {
            SingleOptionSelector(
                title = "Select Added Date:",
                options = AddedOptions,
                selectedOption = selectedAdded.firstOrNull() ?: "",
                onOptionToggle = { label ->
                    filterViewModel.setSelectedAdded(label?.let { listOf(it) } ?: emptyList())
                }
            )
        }

        // Added Photo
        SettingsItem("Added Photo:", selectAddedPhoto) {
            visibleSelector = toggleSelector(visibleSelector, VisibleSelector.Added_photo)
        }
        AnimatedVisibility(visibleSelector == VisibleSelector.Added_photo) {
            SingleOptionSelector(
                title = "Select Photo Option:",
                options = AddedPhotoOptions,
                selectedOption = selectedAddedPhoto.firstOrNull() ?: "",
                onOptionToggle = { label ->
                    filterViewModel.setSelectedAddedPhoto(label?.let { listOf(it) } ?: emptyList())
                }
            )
        }

        // Expiration Date
        SettingsItem("Expiration Date:", selectedExpirationDate.firstOrNull()?.let { expirationDateLabels[it] } ?: "") {
            visibleSelector = toggleSelector(visibleSelector, VisibleSelector.Expiration_Date)
        }
        AnimatedVisibility(visibleSelector == VisibleSelector.Expiration_Date) {
            val labelList = expirationDateLabels.values.toList()
            val valueList = expirationDateLabels.keys.toList()
            SingleOptionSelector(
                title = "Select Expiration Date:",
                options = labelList,
                selectedOption = selectedExpirationDate.firstOrNull()?.let { expirationDateLabels[it] } ?: "",
                onOptionToggle = { label ->
                    val index = labelList.indexOf(label)
                    val selected = if (index >= 0) listOf(valueList[index]) else emptyList()
                    filterViewModel.setSelectedExpirationDate(selected)
                }
            )
        }
    }
}

// Utility function
private fun toggleSelector(current: VisibleSelector, target: VisibleSelector): VisibleSelector {
    return if (current == target) VisibleSelector.NONE else target
}
private fun truncateToMidnight(timestamp: Long): Long {
    val calendar = java.util.Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}
