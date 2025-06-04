@file:OptIn(ExperimentalMaterial3Api::class)

package com.LingTH.fridge.Barcode;

import Databases.AddViewModelFactory
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import Databases.Addviewmodel
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import com.LingTH.fridge.MainActivity2
import com.LingTH.fridge.Setting.components.OptionSelector
import com.LingTH.fridge.ui.theme.getAdaptiveHorizontalPadding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class Add : ComponentActivity() {

    private lateinit var viewModel: Addviewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and ViewModel before setContent
        val barcode = intent?.getStringExtra("barcode") ?: "No barcode found"
        val db = InventoryDatabase.getDatabase(applicationContext) // ✅ Use singleton getter
        val productDao = db.productDao()

        val factory = AddViewModelFactory(productDao)

        lifecycleScope.launch {
            val list = productDao.getAllProducts().first()
            Log.d("DB", "First load: ${list.size} items")
        }

        viewModel = ViewModelProvider(this, factory)[Addviewmodel::class.java] // ✅ Safe initialization

        setContent {
            ProductScreen(barcode = barcode, viewModel = viewModel)
        }
    }
}



@Composable
fun ProductScreen(barcode: String, viewModel: Addviewmodel) {
    Log.d("ProductScreen", "Scanned barcode: $barcode")

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf<String?>(null) }
    var addDay by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    // Removed LocalConfiguration and related variables for padding

    LaunchedEffect(barcode) {
        isLoading = true
        errorMessage = ""
        try {
            val product = getProductData(barcode)
            Log.d("ProductScreen", "passsss")
            product?.let {
                name = it.product_name
                categories = it.categories
                imageUrl = it.image_url
                expirationDate = it.expiration_date?.toDateString() ?: ""
                addDay = it.add_day?.toDateString() ?: ""
                notes = it.notes ?: ""

                Log.d("Databases.ProductData", "Name: $name, Categories: $categories, Image URL: $imageUrl")
            }
                ?: run {
                    errorMessage = "Product not found or failed to fetch."
                }
        } catch (e: Exception) {
            errorMessage = "Error loading product: ${e.message}"
            Log.e("ProductScreen", "Error loading product", e)
        } finally {
            isLoading = false
        }
    }


    // Main UI
    CenterAlignedTopAppBarExample( // This is the Scaffold
        barcode = barcode,
        name = name,
        categories = categories,
        imageUrl = imageUrl,
        newImageUri = newImageUri,
        expirationDate = expirationDate,
        notes = notes,
        viewModel = viewModel,
        context = context,
        onBackClick = {
            val intent = Intent(context, MainActivity2::class.java)
            context.startActivity(intent)
        }
    ) { innerPadding -> // Content lambda for the Scaffold
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply Scaffold's padding
                .padding(horizontal = getAdaptiveHorizontalPadding()) // Use adaptive padding utility
                .fillMaxSize()
            // Add .verticalScroll(rememberScrollState()) if content exceeds screen height
        ) {
            // Spacer(modifier = Modifier.height(16.dp)) // Original spacer, adjust if needed

            AddPhotoButton(
                imageUrl = imageUrl,
                currentUri = newImageUri,
                onImageUriChanged = { uri -> newImageUri = uri },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmailInputExample(
                productName1 = name,
                onValueChange = { name = it }
            )

            SettingsScreenadd(
                categories = categories,
                onValueChange = { newCategories -> categories = newCategories }
            )

            ExpirationDateSelector(
                onDateChange = { expirationDate = it }
            )

            DayAdd(
                selectedDay = addDay,
                onDayChange = { addDay = it }
            )

            Notes(
                notes = notes,
                onValueChange = { notes = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Log.d("imon" , "have $addDay $")

        }

    }
}


fun Long.toDateString(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(Date(this))
}


fun String.toEpochMillis(): Long {
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        format.isLenient = false
        format.parse(this)?.time ?: 0L
    } catch (e: Exception) {
        Log.e("ProductScreen", "Date parsing failed for: $this", e)
        0L
    }
}

var isSaved = true
fun saveProductIfValid(
    viewModel: Addviewmodel,
    barcode: String,
    name: String,
    categories: String,
    imageUrl: String,
    expirationDate: String?,
    notes: String,
    context: Context,
    onComplete: () -> Unit
) {
    if (isSaved) {
        if (categories.isBlank()) {
            Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        val todayDate = getTodayDate()
        val addDayMillis = todayDate.toEpochMillis()
        Log.d("imon", "$todayDate")

        if (expirationDate.isNullOrEmpty()) {
            Toast.makeText(context, "Please select Expiration Date", Toast.LENGTH_SHORT).show()
            return
        }

        val expirationMillis = expirationDate.toEpochMillis()

        if (addDayMillis == 0L || expirationMillis == 0L) {
            Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUrl.startsWith("content://")) {
            try {
                val uri = Uri.parse(imageUrl)
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to persist image permission", Toast.LENGTH_SHORT).show()
                return
            }
        }

        viewModel.saveProduct(
            barcode = barcode,
            name = name,
            categories = categories,
            imageUrl = imageUrl,
            add_day = addDayMillis,
            expie_day = expirationMillis,
            notes = notes,
            onSaved = {
                Toast.makeText(context, "Product saved successfully", Toast.LENGTH_SHORT).show()
                onComplete()
            }
        )
    }
    isSaved = false
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBarExample(
    barcode: String,
    name: String,
    categories: String,
    imageUrl: String,
    newImageUri: Uri?,
    expirationDate: String?,
    notes: String,
    viewModel: Addviewmodel,
    context: Context,
    onBackClick: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context1 = LocalContext.current
    val activity = context1 as? Activity

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Enter Product Information",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    BackButton(onBackClick)
                },
                actions = {
                    Text(
                        text = "Done",
                        fontSize = 18.sp,
                        modifier = Modifier.clickable {
                            val finalImageUrl = newImageUri?.toString() ?: imageUrl
                            if (finalImageUrl.startsWith("content://")) {
                                try {
                                    context.contentResolver.takePersistableUriPermission(
                                        Uri.parse(finalImageUrl),
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            isSaved = true
                            saveProductIfValid(
                                viewModel = viewModel,
                                barcode = barcode,
                                name = name,
                                categories = categories,
                                imageUrl = finalImageUrl,
                                expirationDate = expirationDate,
                                notes = notes,
                                context = context,
                                onComplete = {
                                    Log.d("onComplete", "called")
                                    val intent = Intent(activity, MainActivity2::class.java).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    }
                                    activity?.startActivity(intent)
                                    activity?.finish()
                                }
                            )
                        }
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        content = content
    )
}




@Composable
fun BackButton(onBackClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
        // Modifier.clickable {} removed
        // Modifier.padding(start = 16.dp) removed
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))

        }
    }
}

@Composable
fun AddPhotoButton(
    imageUrl: String?, // Initial URL from server/ (the one in ProductScreen's `imageUrl` state)
    currentUri: Uri?,    // The URI of an image newly picked by the user (ProductScreen's `newImageUri` state)
    onImageUriChanged: (Uri?) -> Unit, // Callback to inform ProductScreen when a new image is picked
    modifier: Modifier = Modifier
) {
    // No longer need local selectedImageUri state here, as ProductScreen will manage it via `currentUri` and `onImageUriChanged`
    // var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageUriChanged(uri) // Notify ProductScreen about the new URI
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.8f) // Changed width
            .aspectRatio(16 / 9f) // Added aspect ratio
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFFD3D3D3))
            .clickable { imagePickerLauncher.launch("image/*") }, // Launch image picker
        contentAlignment = Alignment.Center
    ) {
        when {
            currentUri != null -> { // If a new image has been picked, display it
                Image(
                    painter = rememberAsyncImagePainter(currentUri),
                    contentDescription = "Selected product image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            imageUrl != null && imageUrl.isNotBlank() -> { // Otherwise, if an existing imageUrl exists, display it
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Product image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> { // Placeholder if no image
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Image, contentDescription = null)
                    Text("Add a photo of your product.", color = Color.Gray)
                }
            }
        }
    }
}
// Removed ScrollContent as it's no longer used directly here or is implicit by passing content lambda

@Composable
fun EmailInputExample(productName1: String, onValueChange: (String) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    Email(
        label = "Product Name",
        productName = productName1, // Directly use the passed-in value
        onProductNameChange = { onValueChange(it) }, // Pass changes back
        isVisible = isVisible,
        onToggleVisible = { isVisible = !isVisible }
    )
}


@Composable
fun Email(label: String, productName: String, onProductNameChange: (String) -> Unit, isVisible: Boolean, onToggleVisible: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isVisible) {
        if (isVisible) focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleVisible() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 14.sp, color = Color.Black, modifier = Modifier.weight(0.4f))

            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                if (isVisible) {
                    BasicTextField(
                        value = productName,
                        onValueChange = onProductNameChange,
                        singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp, color = Color.DarkGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                } else {
                    Text(text = productName.ifEmpty { "" }, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpirationDateSelector(
    selectedDate: String? = null,
    onDateChange: (String) -> Unit
) {
    val currentDateMillis = System.currentTimeMillis()
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.let { convertDateToMillis(it) } ?: currentDateMillis
    )

    Column(modifier = Modifier.padding(0.dp)) {
        inputNotFile(
            label = "Expiration Date",
            value = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: "Select Date",
            onToggleVisible = { showDatePickerDialog = true }
        )

        if (showDatePickerDialog) {
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePickerDialog = false
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = convertMillisToDate(millis)
                            Log.d("imon", "Selected date in millis: $millis")
                            Log.d("imon", "Converted date: $date")
                            onDateChange(date)
                        } else {
                            Log.e("imon", "No date selected in DatePicker")
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePickerDialog = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState, showModeToggle = false)
            }
        }
    }
}

fun convertDateToMillis(date: String): Long? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val localDate = LocalDate.parse(date, formatter)
        localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    } catch (e: Exception) {
        null
    }
}

fun convertMillisToDate(millis: Long): String {
    return try {
        val localDate = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
    } catch (e: Exception) {
        "Invalid date"
    }
}






@Composable
fun DayAdd(
    selectedDay: String? = null,
    onDayChange: (String) -> Unit
) {
    val today = getTodayDate()

    var day by remember { mutableStateOf(selectedDay ?: today) }

    LaunchedEffect(Unit) {
        if (selectedDay == null) {
            onDayChange(today)
        }
    }

    Column(modifier = Modifier.padding(1.dp)) {
        inputNotFile(
            label = "Add Day",
            value = day,
            onToggleVisible = {
                // Optional: add DatePicker if needed
            }
        )
    }
}




@Composable
fun inputNotFile(label: String, value: String, onToggleVisible: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            .clickable { onToggleVisible() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(0.4f)
            )

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = value.ifEmpty { "Select Category" },
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun Notes(
    notes: String,
    onValueChange: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    Email(
        label = "Notes:",
        productName = notes,
        onProductNameChange = onValueChange,
        isVisible = isVisible,
        onToggleVisible = { isVisible = !isVisible }
    )
}


fun getTodayDate(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date())
}

@Composable
fun SettingsScreenadd(
    categories: String,
    onValueChange: (String) -> Unit
) {
    var visibleSelector by remember { mutableStateOf(VisibleSelector.NONE) }

    val selectAlertbeforeEX = remember { mutableStateListOf<String>() }

    // Load existing categories once when Composable loads
    LaunchedEffect(categories) {
        if (selectAlertbeforeEX.isEmpty() && categories.isNotBlank()) {
            val initial = categories
                .split(",")
                .map { it.trim().removePrefix("en:") }
                .filter { it.isNotBlank() }
            selectAlertbeforeEX.clear()
            selectAlertbeforeEX.addAll(initial)
        }
    }


    val alertOptions = selectAlertbeforeEX.toList() + "+"


    var isAddingCustomOption by remember { mutableStateOf(false) }
    var customOptionText by remember { mutableStateOf("") }

    val selectedText = selectAlertbeforeEX.joinToString(", ")

    inputNotFile(
        label = "Category:",
        value = selectedText
    ) {
        visibleSelector = if (visibleSelector == VisibleSelector.ALERT_BEFORE_EXPIRED)
            VisibleSelector.NONE
        else
            VisibleSelector.ALERT_BEFORE_EXPIRED
    }

    AnimatedVisibility(visible = visibleSelector == VisibleSelector.ALERT_BEFORE_EXPIRED) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OptionSelector(
                        title = "Select a category:",
                        options = alertOptions,
                        selectedOptions = selectAlertbeforeEX
                    ) { option ->
                        if (option == "+") {
                            isAddingCustomOption = true
                        } else {
                            if (selectAlertbeforeEX.contains(option)) {
                                selectAlertbeforeEX.remove(option)
                            } else {
                                selectAlertbeforeEX.add(option)
                            }

                            val updatedCategories = selectAlertbeforeEX.joinToString(", ")
                            onValueChange(updatedCategories)
                        }
                    }

                }
            }

            if (isAddingCustomOption) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TextField(
                            value = customOptionText,
                            onValueChange = { customOptionText = it },
                            label = { Text("Enter new category") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (customOptionText.isNotBlank()) {
                                    selectAlertbeforeEX.add(customOptionText)
                                    customOptionText = ""
                                    isAddingCustomOption = false

                                    val updatedCategories = selectAlertbeforeEX.joinToString(", ") { it }
                                    onValueChange(updatedCategories)
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}






enum class VisibleSelector {
    NONE,
    ALERT_BEFORE_EXPIRED

}
@Composable
fun getAdaptiveHorizontalPadding(): Dp {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 360 -> 8.dp
        configuration.screenWidthDp < 600 -> 16.dp
        else -> 32.dp
    }
}

