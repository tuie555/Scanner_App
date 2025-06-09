package com.LingTH.fridge.sortandfilter.Setting.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun SettingsTopBar(navController: NavHostController) {
    TopAppBar(
        backgroundColor = Color.White,
        elevation = 0.dp,
        modifier = Modifier.height(56.dp),
        title = {
            Box(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)) {
                Text("Settings", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color.Black)
            }
        }
    )
}