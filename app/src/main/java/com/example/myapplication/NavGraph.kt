package com.example.myapplication

import Databases.ProductData
import ViewModels.SettingsViewModelFactory
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.setting.SettingsScreen
import com.example.myapplication.sortandfilter.SandFscreen
import viewmodel.SettingsViewModel

@Composable
fun NavigationGraph(navController: NavHostController, products: List<ProductData>, paddingValues: PaddingValues, searchText: String) {
    NavHost(navController, startDestination = "productList") {
        composable("productList") { ProductListScreen(products, navController, paddingValues, searchText) }
        composable("settings") {
            val context = LocalContext.current
            val database = InventoryDatabase.getDatabase(context)
            val settingsDao = database.settingsDao()
            val viewModelFactory = SettingsViewModelFactory(settingsDao)
            val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)

            SettingsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("Sorting and Filter") { SandFscreen(navController) }
    }
}


