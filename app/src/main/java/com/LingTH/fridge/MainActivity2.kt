package com.LingTH.fridge


import Databases.ProductData
import InventoryDatabase
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.LingTH.fridge.Notification.BootReceiver
import com.LingTH.fridge.Barcode.Edit
import com.LingTH.fridge.Barcode.Scanner
import com.LingTH.fridge.sortandfilter.FilterViewModel
import com.LingTH.fridge.sortandfilter.FilterViewModelFactory


class MainActivity2 : ComponentActivity() {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (!isGranted) {
                    Toast.makeText(this, "คุณปฏิเสธการอนุญาตแจ้งเตือน", Toast.LENGTH_SHORT).show()
                }
            }

            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Ok
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    AlertDialog.Builder(this)
                        .setTitle("แจ้งเตือนสำคัญ")
                        .setMessage("แอปต้องการสิทธิ์การแจ้งเตือนเพื่อให้แจ้งเตือนวันหมดอายุของสินค้า")
                        .setPositiveButton("อนุญาต") { _, _ ->
                            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                        .setNegativeButton("ยกเลิก", null)
                        .show()
                }

                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        scheduleRepeatingAlarm(this)




        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val isSettingsScreen = navBackStackEntry?.destination?.route == "settings"
            val isFilterScreen = navBackStackEntry?.destination?.route == "Sorting and Filter"

            var searchText by remember { mutableStateOf("") } // This was from the previous state, let's keep it for now if FilterViewModel not updated yet
            val context = LocalContext.current
            val database = InventoryDatabase.getDatabase(context)
            val productDao = database.productDao()
            val filterViewModel: FilterViewModel = viewModel(
                factory = FilterViewModelFactory(productDao)
            )
            LaunchedEffect(searchText) {
                filterViewModel.setSearchText(searchText)

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = "test_channel"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Test Channel",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    notificationManager.createNotificationChannel(channel)
                }
            }

            Scaffold(
                topBar = {
                    if (!isSettingsScreen) {
                        TopBar(
                            searchText = searchText,
                            onSearchTextChange = { searchText = it },
                            isFilterScreen = isFilterScreen,
                            onFilterChanged = { isFiltering ->
                                if (isFiltering) {
                                    navController.navigate("Sorting and Filter") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                } else {
                                    // Assuming navigating back from filter screen goes to productList or similar
                                    navController.popBackStack()
                                }
                            }
                        )
                    } else {
                        null
                    }
                },
                bottomBar = {
                    BottomBar(navController, isSettingsScreen) { isSettings ->
                        if (isSettings) {
                            navController.navigate("settings") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                }
            ) { paddingValues ->
                NavigationGraph(
                    navController = navController,
                    paddingValues = paddingValues,
                    filterViewModel = filterViewModel
                )
            }
        }
    }
}
    // Optional: ตรวจผลการขอ permission









@Composable
fun TopBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    isFilterScreen: Boolean,
    onFilterChanged: (Boolean) -> Unit,
) {
    val insets = WindowInsets.statusBars.asPaddingValues()

    TopAppBar(
        backgroundColor = Color.White,
        elevation = 0.dp,
        modifier = Modifier
            .padding(top = insets.calculateTopPadding())
            .fillMaxWidth()
            .height(64.dp),
        title = {
            if (!isFilterScreen) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iconapp),
                        contentDescription = "Logo Icon",
                        modifier = Modifier
                            .size(60.dp)
                            .padding(end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .background(
                                color = Color(0xFFF0F0F0),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchText,
                                onValueChange = onSearchTextChange,
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color.Black,
                                    fontSize = 14.sp
                                ),
                                decorationBox = { innerTextField ->
                                    if (searchText.isEmpty()) {
                                        Text(
                                            "Search",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Sorting and Filtering",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    if (isFilterScreen) {
                        onFilterChanged(false) // ปิดหน้า Filter
                    } else {
                        onFilterChanged(true)  // เปิดหน้า Filter
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = "Filter",
                    tint = Color.Black
                )
            }
        }
    )
}







@Composable
fun ProductCard(
    product: ProductData,
    onClick: () -> Unit,
) {
    val tagsToCheck = listOf("water", "snack", "food")

    // แยก categories เป็น list แก้ lower case
    val categoriesList = product.categories.split(",").map { it.trim().lowercase() }

    // หา tag ที่ contain คำใน tagsToCheck
    val selectedCategory = categoriesList.firstOrNull { category ->
        tagsToCheck.any { tag -> category.contains(tag) }
    } ?: categoriesList.firstOrNull() ?: ""

    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .heightIn(min = 160.dp, max = 240.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = selectedCategory,
                    fontSize = 12.sp,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SmartImageLoader(
                    imagePath = product.image_url,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f)
                        .align(Alignment.Center)
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





@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BottomBar(
    navController: NavHostController,
    isSettingsScreen: Boolean,
    onSettingsChanged: (Boolean) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val iconSize = (screenWidth * 0.08f).coerceIn(40.dp, 50.dp)
    val context = LocalContext.current

    Box {
        BottomAppBar(
            backgroundColor = Color.White,
            elevation = 0.dp,
            contentPadding = PaddingValues(horizontal = 20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .drawBehind {
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
        ) {
            // ปุ่ม Product List
            IconButton(
                onClick = {
                    if (isSettingsScreen) {
                        navController.popBackStack() // กลับหน้าก่อนหน้า (productList)
                        onSettingsChanged(false)
                    } else {
                        navController.navigate("productList") {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Menu",
                    tint = if (!isSettingsScreen) Color.Black else Color(0xFF6B7280),
                    modifier = Modifier.size(iconSize)
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // เว้นที่ไว้กลางเพื่อ FAB

            Spacer(modifier = Modifier.weight(1f)) // เว้นที่ไว้กลางเพื่อ FAB

            // ปุ่ม Settings
            IconButton(
                onClick = {
                    if (!isSettingsScreen) {
                        onSettingsChanged(true)
                        navController.navigate("settings")
                    } else {
                        onSettingsChanged(false)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (isSettingsScreen) Color.Black else Color(0xFF6B7280),
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        // ปุ่มกลาง FAB
        CentralFab(
            onClick = {
                context.startActivity(Intent(context, Scanner::class.java))
            }
        )
    }
}


@Composable
fun CentralFab(onClick: () -> Unit) {
    val blue400 = Color(0xFF6B82A8)
    Box(
        modifier = Modifier
            .fillMaxWidth() // Ensure the Box takes full width to center the FAB
            .height(70.dp), // Match BottomAppBar height for alignment
        contentAlignment = Alignment.TopCenter // Align FAB to the TopCenter of this Box
    ) {
        Box(
            modifier = Modifier
                // .align(Alignment.TopCenter) // This align is for the parent Box
                .offset(y = (-40).dp)
                .shadow(30.dp, RoundedCornerShape(30.dp), clip = false)
                .clip(RoundedCornerShape(30.dp))
                .background(blue400)
                .clickable { onClick() }
                .size(width = 130.dp, height = 70.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .border(width = 4.dp, color = Color.Black, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black,
                    modifier = Modifier.size(45.dp)
                )
            }
        }
    }
}


fun scheduleRepeatingAlarm(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, BootReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val intervalMillis = 10_000L // 10 วินาที

    val startTime = System.currentTimeMillis() + 5_000L // เริ่มหลังจากนี้ 5 วิ

    // 🔁 ตั้ง alarm แบบทำซ้ำ (ในบางรุ่นอาจไม่แม่น แต่ใช้ได้ดีพอสำหรับกรณีทั่วไป)
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        startTime,
        intervalMillis,
        pendingIntent
    )
}
@Composable
fun SmartImageLoader(
    imagePath: String,
    modifier: Modifier = Modifier
) {


    if (imagePath.isBlank()) {
        // รูป default ถ้า imagePath ว่าง
        Image(
            painter = painterResource(id = R.drawable.iconapp),
            contentDescription = "Default Image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        val painter = rememberAsyncImagePainter(
            model = imagePath,
            error = painterResource(id = R.drawable.iconapp), // กรณีโหลดไม่สำเร็จ
            placeholder = painterResource(id = R.drawable.iconapp)
        )

        Image(
            painter = painter,
            contentDescription = "Product Image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}
@SuppressLint("ConfigurationScreenWidthHeight")
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
                    val intent = Intent(navController.context, Edit::class.java)
                    intent.putExtra("productData", product)
                    navController.context.startActivity(intent)
                }
            )
        }
    }
}