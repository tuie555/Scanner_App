package com.example.myapplication

import ProductData
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.setting.SettingsScreen
import com.example.myapplication.sortandfilter.SandFscreen

@Composable
fun NavigationGraph(navController: NavHostController, products: List<ProductData>, paddingValues: PaddingValues, searchText: String) {
    NavHost(navController, startDestination = "productList") {
        composable("productList") { ProductListScreen(products, navController, paddingValues, searchText) }
        composable("settings") { SettingsScreen(navController) }
        composable("Sorting and Filter") { SandFscreen(navController) }
    }
}


