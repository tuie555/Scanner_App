package com.example.myapplication
import Databases.Productviewmodel
import Databases.ProductData
import Databases.daysUntilExpiry
import ExpiryCheckWorker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.barcode.Edit
import com.example.myapplication.barcode.Scanner
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.myapplication.sortandfilter.FilterViewModel
import com.example.myapplication.sortandfilter.FilterViewModelFactory
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import androidx.appcompat.app.AlertDialog
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity2 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = InventoryDatabase.getDatabase(this)
        val settingsDao = db.settingsDao()

        lifecycleScope.launch {
            val settings = settingsDao.getSettings()

            if (settings != null) {
                val repeatHours = settings.repeatAlert.toIntOrNull() ?: 4
                scheduleRepeatingWork(this@MainActivity2, repeatHours)
            }
        }

        requestNotificationPermission()

        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            AlertDialog.Builder(this)
                .setTitle("Notification is Disabled")
                .setMessage("Please enable notifications in system settings to receive alerts.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    val intent = Intent().apply {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }



        setContent {
            val navController = rememberNavController()
            var isSettingsScreen by remember { mutableStateOf(false) }
            var searchText by remember { mutableStateOf("") }
            var isFilterScreen by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val viewmodel: Productviewmodel = viewModel (factory = ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application))
            val products by viewmodel.productFlow.collectAsState(initial = emptyList())
            val database = InventoryDatabase.getDatabase(context)
            val productDao = database.productDao()
            val filterViewModel: FilterViewModel = viewModel(
                factory = FilterViewModelFactory(productDao)
            )
            LaunchedEffect(searchText) {
                filterViewModel.setSearchText(searchText)
                filterViewModel.setSearchText(searchText)

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = "test_channel"

                val channel = NotificationChannel(channelId, "Test Channel", NotificationManager.IMPORTANCE_HIGH)
                    notificationManager.createNotificationChannel(channel)


                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setContentTitle("Test Notification")
                    .setContentText("This is a test notification.")
                    .build()

                notificationManager.notify(1, notification)
            }

            Scaffold(
                topBar = {
                if (!isSettingsScreen)   {
                    TopBar(
                        searchText = searchText,
                        onSearchTextChange = { searchText = it } ,
                        isFilterScreen = isFilterScreen,
                        onFilterChanged = { isFilterScreen = it },
                        navController = navController)

                }
                         else{
                             null
                         }},
                bottomBar = {
                    BottomBar(navController, isSettingsScreen) { isSettings ->
                        isSettingsScreen = isSettings
                    }
                }
            ) { paddingValues ->
                NavigationGraph(
                    navController = navController,
                    paddingValues = paddingValues,
                    searchText = searchText,
                    filterViewModel = filterViewModel
                )
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // เรียก launcher เพื่อขอ permission อย่างถูกต้อง
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // ได้รับ permission แล้ว — เรียกใช้ testNotification ได้เลย
            testNotification()
        }
    }

    // Optional: ตรวจผลการขอ permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Permission", "✅ Notification permission granted")
            testNotification()
        } else {
            Log.w("Permission", "❌ Notification permission denied")
        }
    }

    // สมมุติว่าเป็นฟังก์ชันแสดง Notification
    private fun testNotification() {
        // แสดง notification หรือ logic อื่น ๆ
        Log.d("Notification", "🔔 Showing test notification")
    }

}

@Composable
fun ProductListScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: FilterViewModel
) {
    val filteredProducts by viewModel.filteredProducts.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val columnCount = when {
        screenWidth < 600.dp -> 2
        screenWidth < 900.dp -> 3
        else -> 4
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(filteredProducts) { product ->
            ProductCard(
                product = product,
                onClick = {
                    Log.d("DEBUG", "Clicked ${product.product_name}")
                    val intent = Intent(navController.context, Edit::class.java)
                    intent.putExtra("productData", product)
                    navController.context.startActivity(intent)
                }
            )
        }
    }
}




@Composable
fun TopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    isFilterScreen: Boolean,
    onFilterChanged: (Boolean) -> Unit,
    navController: NavHostController
) {
    val insets = WindowInsets.statusBars.asPaddingValues()
    var isSearchBarVisible by remember { mutableStateOf(false) }

    Column {
        TopAppBar(
            backgroundColor = Color.White,
            elevation = 0.dp,
            modifier = Modifier
                .padding(top = insets.calculateTopPadding()),
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isFilterScreen) "Sorting and Filtering" else "Your Product List", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
                }
            },
            actions = {
                IconButton(onClick = {


                    if (isFilterScreen) {
                        navController.popBackStack()
                        onFilterChanged(false)
                    } else {
                        navController.navigate("Sorting and Filter")
                        onFilterChanged(true)
                    }
                }) {
                    Icon(imageVector = Icons.Default.FilterAlt, contentDescription = "Filter", tint = Color.Black)
                }
                IconButton(onClick = {
                    isSearchBarVisible = !isSearchBarVisible // Toggle search bar visibility
                }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.Black)
                }
                Spacer(Modifier.width(4.dp))
            }
        )

        // Show the search bar below the TopAppBar
        if (isSearchBarVisible) {
            TextField(
                value = searchText,
                onValueChange = { onSearchTextChange(it) },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                placeholder = {
                    Text("Search...", color = Color.Gray, fontSize = 14.sp)
                },// Update the search text in the parent
                shape = RoundedCornerShape(30.dp), // Set the shape to oval
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp) // Set a smaller height for the TextField
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black, // Set text color to black
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent, // Remove underline when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove underline when unfocused
                    placeholderColor = Color.Gray // Set placeholder color to gray
                )

            )
        }
    }
}

@Composable
fun ProductCard(
    product: ProductData,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .heightIn(min = 160.dp, max = 240.dp) // Modified: Removed fixed height, added min/max
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            .clickable { onClick() }, // ✅ ใช้ onClick ที่เรารับเข้ามา
        elevation = 4.dp
    )
    {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = product.categories,
                    fontSize = 12.sp,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                // Modified: Image to be scalable and centered
                SmartImageLoader(
                    imagePath = product.image_url,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f)
                        .align(Alignment.Center) // Ensure it's centered within the Box
                )

            }

            Row(
                modifier = Modifier
                    .background(Color(0xFFBBDEFB))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.product_name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }


            }
        }
    }
}
@Composable
fun SmartImageLoader(imagePath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    when {
        imagePath.startsWith("http") -> {
            // โหลดจากเว็บ (URL)
            Image(
                painter = rememberAsyncImagePainter(imagePath),
                contentDescription = "Image from web",
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }

        imagePath.startsWith("content://") -> {
            // โหลดจาก URI (ในเครื่อง)
            val uri = remember(imagePath) { Uri.parse(imagePath) }
            val bitmap = remember(uri) {
                try {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                } catch (e: Exception) {
                    null
                }
            }

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Local image",
                    modifier = modifier,
                    contentScale = ContentScale.Crop
                )
            } ?: Box(modifier.background(Color.Gray)) {
                Text("โหลดรูปไม่ได้", color = Color.White, modifier = Modifier.padding(8.dp))
            }
        }

        else -> {
            // ไม่รู้จักฟอร์แมต
            Box(modifier.background(Color.LightGray)) {
                Text("ไม่รองรับ", color = Color.Red, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController,isSettingsScreen: Boolean,
              onSettingsChanged: (Boolean) -> Unit) {
    val blue400 = Color(0xFF6B82A8)
    val blue500 = Color(0xFF5A6D9E)
    val blue600 = Color(0xFF4A5A85)
    val context = LocalContext.current
    var isClicked by remember { mutableStateOf(false) }
    BottomAppBar(
        backgroundColor = Color.White,
        elevation = 8.dp,
        contentPadding = PaddingValues(horizontal = 24.dp),
        modifier = Modifier.height(70.dp)
    ) {
        IconButton(
            onClick = { context.startActivity(Intent(context, MainActivity2::class.java)) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Menu",
                tint = Color.Black,
                modifier = Modifier.size(24.dp) // Adjusted size
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        // Oval Floating Action Button
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 60.dp) // Set width and height for oval shape
                .clip(RoundedCornerShape(28.dp)) // Use RoundedCornerShape to create an oval
                .background(blue400) // Background color
                .clickable(onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            Scanner::class.java
                        )
                    )
                }), // Handle click
            contentAlignment = Alignment.Center // Center the icon
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(30.dp), // Adjusted size
                tint = Color.White // Icon color
            )

        }

            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {

                    if (isSettingsScreen) {
                        navController.popBackStack()
                        onSettingsChanged(false)
                    } else {
                        navController.navigate("settings")
                        onSettingsChanged(true)
                    }
                },

                modifier = Modifier.size(40.dp) // Change background color
            )
            {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "settings",
                    tint = (if (isClicked) Color.Black else Color(0xFF6B7280)),
                    modifier = Modifier.size(24.dp) // Adjusted size
                )
            }

        }
    }
fun scheduleRepeatingWork(context: Context, intervalHours: Int) {
    val workRequest = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(
        intervalHours.toLong(), TimeUnit.HOURS
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "ExpiryCheckWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}





