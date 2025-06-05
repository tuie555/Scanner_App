package com.LingTH.fridge

import com.LingTH.fridge.Setting.viewmodel.SettingsViewModelFactory
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.LingTH.fridge.Setting.SettingsScreen
import com.LingTH.fridge.sortandfilter.FilterViewModel
import com.LingTH.fridge.sortandfilter.FilterViewModelFactory
import com.LingTH.fridge.sortandfilter.SandFscreen
import com.LingTH.fridge.Setting.viewmodel.SettingsViewModel

// NavigationGraph.kt
@Composable
fun NavigationGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    filterViewModel: FilterViewModel
) {
    val context = LocalContext.current
    val database = InventoryDatabase.getDatabase(context)

    NavHost(navController = navController, startDestination = "productList") {
        composable("productList") {
            ProductListScreen(
                navController = navController,
                paddingValues = paddingValues,
                viewModel = filterViewModel
            )
        }

        composable("settings") {
            val settingsDao = database.settingsDao()
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(settingsDao)
            )
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel
            )
        }

        composable("Sorting and Filter") {
            SandFscreen(
                filterViewModel = filterViewModel,
                navController = navController
            )
        }
    }
}



