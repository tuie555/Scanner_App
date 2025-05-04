@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.myapplication.barcode

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.*

class Add : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ProductScreen()
            }
        }
    }
}

@Composable
fun ProductScreen() {
    var selectedDate by remember { mutableStateOf("") }

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
    Column(modifier = Modifier.padding(1.dp)) {
        inputNotFile("Add Day", "") {}
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