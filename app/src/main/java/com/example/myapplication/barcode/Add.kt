@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.barcode;

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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
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
import androidx.compose.material3.IconButton
import com.example.myapplication.MainActivity2
import com.example.myapplication.setting.components.OptionSelector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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
    Box(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBarExample(
            barcode = barcode, // ✅ เพิ่ม barcode
            name = name,
            categories = categories,
            imageUrl = imageUrl,
            newImageUri = newImageUri,
            addDay = addDay,
            expirationDate = expirationDate,
            notes = notes,
            viewModel = viewModel,
            context = context,
            onBackClick = {
                val intent = Intent(context, MainActivity2::class.java)
                context.startActivity(intent)}

        )


        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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


fun saveProductIfValid(
    viewModel: Addviewmodel,
    barcode: String,
    name: String,
    categories: String,
    imageUrl: String,
    addDay: String?,
    expirationDate: String?,
    notes: String,
    context: Context,
    onComplete: () -> Unit
) {
    if (addDay.isNullOrEmpty() || expirationDate.isNullOrEmpty()) {
        Toast.makeText(context, "Please select Add Day and Expiration Date", Toast.LENGTH_SHORT).show()
        return
    }

    val addDayMillis = addDay.toEpochMillis()
    val expirationMillis = expirationDate.toEpochMillis()

    if (addDayMillis == 0L || expirationMillis == 0L) {
        Toast.makeText(context, "Invalid date format", Toast.LENGTH_SHORT).show()
        return
    }

    // *** ตรวจสอบและขอสิทธิ์ถาวร ถ้า imageUrl เป็น content:// ***
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



@Composable
fun CenterAlignedTopAppBarExample(
    barcode: String,
    name: String,
    categories: String,
    imageUrl: String,
    newImageUri: Uri?,       // ✅ รับ newImageUri ด้วย
    addDay: String?,
    expirationDate: String?,
    notes: String,
    viewModel: Addviewmodel,
    context: Context,
    onBackClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context1 = LocalContext.current
    val activity = context1 as? Activity
    val coroutineScope = rememberCoroutineScope()
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
                    BackButton(onBackClick) // 👈 ใส่ BackButton ที่นี่
                },
                actions = {
                    Text(
                        text = "Done",
                        fontSize = 18.sp,
                        modifier = Modifier.clickable {
                            Log.d("DEBUG", "Done button clicked")
                            coroutineScope.launch {
                                // ถ้า newImageUri ไม่ null ให้ใช้ newImageUri.toString() แทน imageUrl เก่า
                                val finalImageUrl = newImageUri?.toString() ?: imageUrl
                                // ขอสิทธิ์ถาวรถ้าเป็น content://
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

                                saveProductIfValid(
                                    viewModel = viewModel,
                                    barcode = barcode,
                                    name = name,
                                    categories = categories,
                                    imageUrl = finalImageUrl,
                                    addDay = addDay,
                                    expirationDate = expirationDate,
                                    notes = notes,
                                    context = context,
                                    onComplete = {
                                        Log.d("NAVIGATION", "Navigating to MainActivity2")
                                        activity?.startActivity(Intent(activity, MainActivity2::class.java))
                                        activity?.finish()
                                    }
                                )
                            }
                        }
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}




@Composable
fun BackButton(onBackClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { }
            .padding(start = 16.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Back", fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            .width(300.dp)
            .height(150.dp)
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
@Composable
fun ScrollContent(innerPadding: PaddingValues) {}

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

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.let { convertDateToMillis(it) } ?: currentDateMillis
    )

    val formattedDate = convertMillisToDate(datePickerState.selectedDateMillis ?: currentDateMillis)

    // Trigger `onDateChange` when the date changes
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            onDateChange(convertMillisToDate(millis))
        }
    }

    Column(modifier = Modifier.padding(0.dp)) {
        // 🔹 ช่องแสดงวันที่ที่เลือก
        inputNotFile(
            label = "Expiration Date",
            value = datePickerState.selectedDateMillis?.let { convertMillisToDate(it) } ?: "",
            onToggleVisible = {}
        )

        // 🔹 ปฏิทินถูกซ่อนไปครึ่งหัวบน
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp) // ปรับให้พอดีแค่ปฏิทิน
                .clipToBounds(), // ตัดส่วนที่ล้นไม่ให้แสดง
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(y = (-120).dp) // 👈 ซ่อนหัวด้านบน
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false, // ❌ ไม่ให้ toggle input mode
                )
            }
        }
    }
}
fun convertDateToMillis(date: String): Long {
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.parse(date)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

@Composable
fun DatePickerCard(datePickerState: DatePickerState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clipToBounds(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.offset(y = (-120).dp)) {
            DatePicker(state = datePickerState, showModeToggle = false)
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    if (millis == 0L) return "No date selected"

    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}


@Composable
fun DayAdd(
    selectedDay: String? = null,
    onDayChange: (String) -> Unit
) {
    val today = getTodayDate() // เช่น "2025-05-23"

    var day by remember { mutableStateOf(selectedDay ?: today) }

    // ส่งค่าวันที่ทันทีเมื่อเปิด Composable (ถ้ายังไม่ได้ส่ง)
    LaunchedEffect(Unit) {
        if (selectedDay == null) {
            onDayChange(today)
        }
    }

    Column(modifier = Modifier.padding(1.dp)) {
        inputNotFile(
            label = "Add Day",
            value = day
        ) {
            // สามารถเปิด DatePicker เมื่อผู้ใช้กดได้ ถ้าต้องการ
        }
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
                    text = value.ifEmpty { "Select Date" },
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

    val parsedCategories = remember(categories) {
        categories
            .split(",")
            .map { it.trim() }
            .filter { it.startsWith("en:") }
            .map { it.removePrefix("en:") }
            .distinct()
    }

    // Use mutableStateListOf to preserve state reactivity
    val selectAlertbeforeEX = remember(categories) {
        mutableStateListOf<String>().apply { addAll(parsedCategories) }
    }

    val alertOptions = remember(categories) {
        mutableStateListOf<String>().apply {
            addAll(parsedCategories)
            add("+")
        }
    }

    var isAddingCustomOption by remember { mutableStateOf(false) }
    var customOptionText by remember { mutableStateOf("") }

    val selectedText = selectAlertbeforeEX.filter { it.isNotBlank() }.joinToString(", ")

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

                            val updatedCategories = selectAlertbeforeEX.joinToString(", ") { "en:$it" }
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
                                    alertOptions.add(alertOptions.size - 1, customOptionText)
                                    selectAlertbeforeEX.add(customOptionText)
                                    customOptionText = ""
                                    isAddingCustomOption = false

                                    val updatedCategories = selectAlertbeforeEX.joinToString(", ") { "en:$it" }
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