package com.LingTH.fridge.Setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptionSelector(
    title: String,
    options: List<String>,
    selectedOptions: List<String>,
    onOptionToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(24.dp))
            .padding(14.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                val isSelected = selectedOptions.contains(option)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) Color(0xFFFF4D4D) else Color(0xFFF5F5F5))
                        .clickable { onOptionToggle(option) }
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(option, color = if (isSelected) Color.White else Color.DarkGray, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }
    }
}