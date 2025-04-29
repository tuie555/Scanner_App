package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.barcode.Scanner

class MainActivity2 : ComponentActivity() {

    data class Product(
        val category: String,
        val name: String,
        val expiry: String,
        val imageUrl: String
    )

    private val products = listOf(
        Product("Fruit", "Apple", "Expired in 1 week", "https://storage.googleapis.com/a1aa/image/b987ba44-85d4-499b-caeb-87c5c041da66.jpg"),
        Product("Meat", "Bacon Sliced", "Expired in 1 week", "https://storage.googleapis.com/a1aa/image/a9f8b17b-2140-446c-ee34-e83f3c1c285d.jpg"),
        Product("Fruit", "Banana", "Expired in 2 days", "https://storage.googleapis.com/a1aa/image/018fdce6-d1c2-4a69-8364-130f2260c763.jpg"),
        Product("Dairy", "Goat Milk", "Expired in 2 days", "https://storage.googleapis.com/a1aa/image/b1023918-3585-4326-2eda-05892c52270c.jpg"),
        Product("Dairy", "Goat Milk from...", "Expired in 1 week", "https://storage.googleapis.com/a1aa/image/01318380-84d1-4c6a-ffad-5f34d8184585.jpg"),
        Product("Meat", "Human Meat", "Expired in 1 day", "https://storage.googleapis.com/a1aa/image/6d347f5c-9493-4632-cd10-267ad1a4269c.jpg"),
        Product("Meat", "Monkey Meat", "", "https://storage.googleapis.com/a1aa/image/ea400dba-9b4b-448d-be0e-5ba7b8a31932.jpg"),
        Product("Meat", "Chai's Leg", "", "https://storage.googleapis.com/a1aa/image/529651aa-15bf-49ce-f0d0-9bd8793cd9fa.jpg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var isSettingsScreen by remember { mutableStateOf(false) }
            var searchText by remember { mutableStateOf("") }
            var isFilterScreen by remember { mutableStateOf(false) }

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
                NavigationGraph(navController, products, paddingValues, searchText)
            }
        }
    }
}

@Composable
fun ProductListScreen(
    products: List<MainActivity2.Product>,
    navController: NavHostController,
    paddingValues: PaddingValues,
    searchText: String
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val columnCount = when {
        screenWidth < 600.dp -> 2 // For small screens
        screenWidth < 900.dp -> 3 // For medium screens
        else -> 4 // For large screens
    }
    val filteredProducts = if (searchText.isEmpty()) {
        products
    } else {
        products.filter {
            it.name.contains(searchText, ignoreCase = true) || it.category.contains(searchText, ignoreCase = true)
        }
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
                ProductCard(product)
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
fun ProductCard(product: MainActivity2.Product) {
    Card(
        shape = RoundedCornerShape(10.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = product.category,
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
                Image(
                    painter = rememberAsyncImagePainter(product.imageUrl),
                    contentDescription = "${product.name} image",
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Crop // Changed to Crop for better image filling
                )
            }
            Row(
                modifier = Modifier
                    .background(Color(0xFFBBDEFB))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (product.expiry.isNotEmpty()) {
                        Text(
                            text = product.expiry,
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F), // Red color for expiry
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(
                    onClick = { /* More options */ },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.Black
                    )
                }
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
            onClick = { /* Menu action */ },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Menu",
                tint = Color.Black,
                modifier = Modifier.size(52.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        // Oval Floating Action Button
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 60.dp) // Set width and height for oval shape
                .clip(RoundedCornerShape(28.dp)) // Use RoundedCornerShape to create an oval
                .background(blue400) // Background color
                .clickable(onClick = { context.startActivity(Intent(context, Scanner::class.java)) }), // Handle click
            contentAlignment = Alignment.Center // Center the icon
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(55.dp), // Size of the icon
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
                    modifier = Modifier.size(52.dp)
                )
            }

        }
    }


