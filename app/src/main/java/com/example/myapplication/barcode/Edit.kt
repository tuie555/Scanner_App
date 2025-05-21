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
import Databases.ProductData
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.myapplication.MainActivity2
import com.example.myapplication.setting.components.OptionSelector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Edit : ComponentActivity() {

    private lateinit var viewModel: Addviewmodel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val product = intent.getSerializableExtra("productData") as? ProductData
        val db = InventoryDatabase.getDatabase(applicationContext)
        val productDao = db.productDao()
        val factory = AddViewModelFactory(productDao)

        viewModel = ViewModelProvider(this, factory)[Addviewmodel::class.java]

        setContent {
            ProductScreen1(product = product, viewModel = viewModel)
        }
    }
}




@Composable
fun ProductScreen1(product: ProductData?, viewModel: Addviewmodel) {
    if (product == null) {
        Text("No product found")
        return
    }

    val context = LocalContext.current
    var name by remember { mutableStateOf(product.product_name) }
    var categories by remember { mutableStateOf(product.categories) }
    var imageUrl by remember { mutableStateOf(product.image_url) }
    var expirationDate by remember { mutableStateOf(product.expiration_date?.toDateStringEdit() ?: "") }
    var addDay by remember { mutableStateOf(product.add_day?.toDateStringEdit() ?: "") }
    var notes by remember { mutableStateOf(product.notes ?: "") }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBarExampleEdit(
            id = product.id, // ✅ ส่ง id ไปด้วย
            barcode = product.barcode,
            name = name,
            categories = categories,
            imageUrl = imageUrl,
            newImageUri = newImageUri,
            addDay = addDay,
            expirationDate = expirationDate,
            notes = notes,
            viewModel = viewModel,
            context = context
        )



        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            AddPhotoButtonEdit(
                imageUrl = imageUrl,
                currentUri = newImageUri,
                onImageUriChanged = { uri -> newImageUri = uri },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            EmailInputExampleEdit(
                productName1 = name,
                onValueChange = { name = it }
            )

            SettingsScreenaddEdit(
                categories = categories,
                onValueChange = { newCategories -> categories = newCategories }
            )

            ExpirationDateSelectorEdit(
                selectedDate = expirationDate,
                onDateChange = { expirationDate = it }
            )

            DayAddEdit(
                selectedDay = addDay,
                onDayChange = { addDay = it }
            )

            NotesEdit(
                notes = notes,
                onValueChange = { notes = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}




fun Long.toDateStringEdit(): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return format.format(Date(this))
}


fun String.toEpochMillisEdit(): Long {
    return try {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        format.isLenient = false
        format.parse(this)?.time ?: 0L
    } catch (e: Exception) {
        Log.e("ProductScreen", "Date parsing failed for: $this", e)
        0L
    }
}


fun updateProductIfValidEdit(
    id: Int,
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

    val addDayMillis = addDay.toEpochMillisEdit()
    val expirationMillis = expirationDate.toEpochMillisEdit()

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

    if (expirationMillis < addDayMillis) {
        Toast.makeText(context, "Expiration date cannot be before Add date", Toast.LENGTH_SHORT).show()
        return
    }

    viewModel.updateProduct(
        id = id,
        barcode = barcode,
        name = name,
        categories = categories,
        imageUrl = imageUrl,
        add_day = addDayMillis,
        expie_day = expirationMillis,
        notes = notes,
        onUpdated = {
            Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
            onComplete()
        },
        onError = {
            Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show()
        }
    )
}



@Composable
fun CenterAlignedTopAppBarExampleEdit(
    id: Int, // ✅ เพิ่มตรงนี้
    barcode: String,
    name: String,
    categories: String,
    imageUrl: String,
    newImageUri: Uri?,
    addDay: String,
    expirationDate: String,
    notes: String,
    viewModel: Addviewmodel,
    context: Context
)
 {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context1 = LocalContext.current
    val activity = context1 as? Activity
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                ),
                title = {
                    Text("Enter Product Information", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    BackButtonEdit()
                },
                actions = {
                    Text(
                        text = "Done",
                        fontSize = 18.sp,
                        modifier = Modifier.clickable {
                            Log.d("DEBUG", "Done button clicked")
                            coroutineScope.launch {
                                updateProductIfValidEdit(
                                    id = id, // ✅ ใส่ id ที่ได้จาก product ที่โหลดมา
                                    viewModel = viewModel,
                                    barcode = barcode,
                                    name = name,
                                    categories = categories,
                                    imageUrl = newImageUri?.toString() ?: imageUrl,
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
        ScrollContentEdit(innerPadding)
    }
}

@Composable
fun BackButtonEdit() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { }
            .padding(start = 16.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Back", fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun AddPhotoButtonEdit(
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
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(currentUri)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .build()
                    ),
                    contentDescription = "Selected product image",
                    modifier = Modifier.fillMaxSize()
                )
            }
            imageUrl != null && imageUrl.isNotBlank() -> { // Otherwise, if an existing imageUrl exists, display it
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .memoryCachePolicy(CachePolicy.DISABLED) // force reload
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .build()
                    ),
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
fun ScrollContentEdit(innerPadding: PaddingValues) {}

@Composable
fun EmailInputExampleEdit(productName1: String, onValueChange: (String) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    EmailEdit(
        label = "Product Name",
        productName = productName1, // Directly use the passed-in value
        onProductNameChange = { onValueChange(it) }, // Pass changes back
        isVisible = isVisible,
        onToggleVisible = { isVisible = !isVisible }
    )
}


@Composable
fun EmailEdit(label: String, productName: String, onProductNameChange: (String) -> Unit, isVisible: Boolean, onToggleVisible: () -> Unit) {
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
fun ExpirationDateSelectorEdit(
    selectedDate: String? = null,
    onDateChange: (String) -> Unit
) {
    val currentDateMillis = System.currentTimeMillis()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.let { convertDateToMillisEdit(it) } ?: currentDateMillis
    )

    val formattedDate = convertMillisToDateEdit(datePickerState.selectedDateMillis ?: currentDateMillis)

    // Trigger `onDateChange` when the date changes
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            onDateChange(convertMillisToDateEdit(millis))
        }
    }

    Column(modifier = Modifier.padding(0.dp)) {
        // 🔹 ช่องแสดงวันที่ที่เลือก
        inputNotFileEdit(
            label = "Expiration Date",
            value = datePickerState.selectedDateMillis?.let { convertMillisToDateEdit(it) } ?: "",
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

fun convertDateToMillisEdit(date: String): Long {
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.parse(date)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

@Composable
fun DatePickerCardEdit(datePickerState: DatePickerState) {
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

fun convertMillisToDateEdit(millis: Long): String {
    if (millis == 0L) return "No date selected"

    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun DayAddEdit(
    selectedDay: String? = null,
    onDayChange: (String) -> Unit
) {
    val today = getTodayDateEdit()

    // Initialize with `selectedDay` or fallback to today
    var day by remember { mutableStateOf(selectedDay ?: today) }

    Column(modifier = Modifier.padding(1.dp)) {
        inputNotFileEdit(
            label = "Add Day",
            value = day
        ) {
            onDayChange(day) // Use the current value, or trigger date picker externally
        }
    }
}

@Composable
fun inputNotFileEdit(label: String, value: String, onToggleVisible: () -> Unit) {
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
fun NotesEdit(
    notes: String,
    onValueChange: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    EmailEdit(
        label = "Notes:",
        productName = notes,
        onProductNameChange = onValueChange,
        isVisible = isVisible,
        onToggleVisible = { isVisible = !isVisible }
    )
}


fun getTodayDateEdit(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date())
}

@Composable
fun SettingsScreenaddEdit(
    categories: String,
    onValueChange: (String) -> Unit
) {
    var visibleSelector by remember { mutableStateOf(VisibleSelectorEdit.NONE) }

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

    inputNotFileEdit(
        label = "Category:",
        value = selectedText
    ) {
        visibleSelector = if (visibleSelector == VisibleSelectorEdit.ALERT_BEFORE_EXPIRED)
            VisibleSelectorEdit.NONE
        else
            VisibleSelectorEdit.ALERT_BEFORE_EXPIRED
    }

    AnimatedVisibility(visible = visibleSelector == VisibleSelectorEdit.ALERT_BEFORE_EXPIRED) {
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

enum class VisibleSelectorEdit {
    NONE,
    ALERT_BEFORE_EXPIRED

}