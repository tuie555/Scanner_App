package com.LingTH.fridge.setting.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable

fun SettingsItem(label: String, value: String, showIcon: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = 48.dp) // Changed from height(45.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(50.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(0.4f)
                    ,

                     // เว้นระยะจาก Value
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier

                        .padding(end = 12.dp)
                )

            }

            // ✅ ใช้ weight ฝั่ง Value เพื่อขยายพื้นที่
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End // ให้ Value + Icon ไปอยู่ขวาสุด
            ) {
                Text(value, fontSize = 14.sp, color = Color.DarkGray)
                if (showIcon) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color.DarkGray,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 6.dp)
                    )
                }
            }
        }
    }
}