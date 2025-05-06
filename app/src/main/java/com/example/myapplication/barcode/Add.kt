@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.barcode;

import android.app.Activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.ProductData
import com.example.myapplication.setting.components.OptionSelector
import com.example.myapplication.setting.components.SettingsItem
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Add : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Retrieve the barcode from the intent
        val barcode = intent.getStringExtra("barcode") ?: ""

        setContent {
            MyApplicationTheme {
                ProductScreen(barcode) // ✅ Pass the barcode into your composable
            }
        }
    }

}



@Composable
fun ProductScreen(barcode: String) {


    Log.d("ProductScreen", "Scanned barcode: $barcode")
    var product by remember { mutableStateOf<ProductData?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 🔍 Fetch data once when barcode changes
    LaunchedEffect(barcode) {
        val result = getProductData(barcode)
        if (result != null) {
            product = result
        } else {
            errorMessage = "Product not found or failed to fetch."
        }
    }

    // 📝 Log product details when it becomes available
    LaunchedEffect(product) {
        product?.let {
            Log.d("ProductScreen", "🧾 Product Name: ${it.product_name}")
            Log.d("ProductScreen", "📦 Categories: ${it.categories}")
            Log.d("ProductScreen", "🖼️ Image URL: ${it.image_url}")
        }
    }


    ////////////////////////////////////////////////////
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CenterAlignedTopAppBarExample()

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            AddPhotoButton(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))

            EmailInputExample()
            val navController = rememberNavController()
            SettingsScreen(navController = navController)
            ExpirationDateSelector()
            DayAdd()
            Notes()
        }
    }
}

@Composable
fun CenterAlignedTopAppBarExample() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
                    BackButton()
                },
                actions = {
                    Text(text = "Done", fontSize = 18.sp)
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}

@Composable
fun BackButton() {
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
fun AddPhotoButton(modifier: Modifier = Modifier) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    Box(
        modifier = modifier
            .width(300.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(Color(0xFFD3D3D3))
            .clickable { imagePickerLauncher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri != null) {
            Image(painter = rememberAsyncImagePainter(selectedImageUri), contentDescription = null, modifier = Modifier.fillMaxSize())
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Image, contentDescription = null)
                Text("Add a photo of your product.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun ScrollContent(innerPadding: PaddingValues) {}

@Composable
fun EmailInputExample() {
    var productName by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    Email(
        label = "Product Name",
        productName = productName,
        onProductNameChange = { productName = it },
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
fun ExpirationDateSelector() {
    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier.padding(0.dp)) {
        inputNotFile("Date Day", convertMillisToDate(datePickerState.selectedDateMillis ?: 0L)) {}
        DatePickerCard(datePickerState)
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
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun DayAdd() {
    val today = remember { getTodayDate() }
    Column(modifier = Modifier.padding(1.dp)) {
        inputNotFile("Add Day", today) {}
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
fun Notes() {
    var productName by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    Email(
        label = "Notes:",
        productName = productName,
        onProductNameChange = { productName = it },
        isVisible = isVisible,
        onToggleVisible = { isVisible = !isVisible }
    )
}

fun getTodayDate(): String {
    val dateFormat = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    var visibleSelector by remember { mutableStateOf(VisibleSelector.NONE) }
    var selectAlertbeforeEX by remember { mutableStateOf(listOf("")) }
    var alertOptions by remember {
        mutableStateOf(
            mutableListOf("+")
        )
    }

    val selectedText = selectAlertbeforeEX.filter { it.isNotBlank() }.joinToString(", ")

    var isAddingCustomOption by remember { mutableStateOf(false) }
    var customOptionText by remember { mutableStateOf("") }

    SettingsItem("Category:", selectedText) {
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
                            selectAlertbeforeEX = if (option in selectAlertbeforeEX)
                                selectAlertbeforeEX - option
                            else
                                selectAlertbeforeEX + option
                        }
                    }
                }
            }

            if (isAddingCustomOption) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                    alertOptions = alertOptions.toMutableList().apply {
                                        add(size - 1, customOptionText)
                                    }
                                    customOptionText = ""
                                    isAddingCustomOption = false
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