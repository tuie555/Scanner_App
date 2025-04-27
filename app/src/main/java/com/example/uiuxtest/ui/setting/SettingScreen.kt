@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.setting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.setting.components.OptionSelector
import com.example.myapplication.setting.components.SettingsItem
import com.example.myapplication.setting.components.SettingsTopBar
import com.example.myapplication.setting.components.SingleOptionSelector


@Composable
fun SettingsScreen(navController: NavHostController) {
    var visibleSelector by remember { mutableStateOf(VisibleSelector.NONE) }
    var selectAlertbeforeEX by remember { mutableStateOf(listOf("1 day", "3 days", "1 week")) }
    var selectedAlertMode by remember { mutableStateOf(listOf("Normal mode")) }
    var selectRepeatAlert by remember { mutableStateOf(listOf("1")) }
    var email by remember { mutableStateOf("") }

    val selectedText = selectAlertbeforeEX.joinToString(", ")
    val selectedAlertModeText = selectedAlertMode.joinToString(", ")
    val selectedRepeatAlertText = selectRepeatAlert.joinToString(",")

    val alertOptions = listOf(
        "1 day", "2 days", "3 days", "4 days", "5 days", "1 week", "2 weeks",
        "3 weeks", "4 weeks", "1 month", "2 months", "3 months", "6 months"
    )
    val alertModeOptionsList = listOf("Normal mode", "E-Girlfriend Mode", "Aggressive Mode", "Friendly Mode")
    val repeatAlertOptions = listOf("1", "2", "3", "4", "6", "7", "8")
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        SettingsTopBar(navController)

        Column(modifier = Modifier.fillMaxSize()) {
            Text("Notification Setting", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Alert before expired และ Select Alert before expired
            SettingsItem("Alert before expired:", selectedText) {
                visibleSelector = if (visibleSelector == VisibleSelector.ALERT_BEFORE_EXPIRED) VisibleSelector.NONE else VisibleSelector.ALERT_BEFORE_EXPIRED
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.ALERT_BEFORE_EXPIRED) {
                OptionSelector("Select Alert before expired:", alertOptions, selectAlertbeforeEX) { option ->
                    selectAlertbeforeEX = if (option in selectAlertbeforeEX) selectAlertbeforeEX - option else selectAlertbeforeEX + option
                }
            }

            // Alert Mode และ Select Alert Mode
            SettingsItem("Alert Mode:", selectedAlertModeText) {
                visibleSelector = if (visibleSelector == VisibleSelector.ALERT_MODE) VisibleSelector.NONE else VisibleSelector.ALERT_MODE
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.ALERT_MODE) {
                SingleOptionSelector("Select Alert Mode:", alertModeOptionsList, selectedAlertMode.firstOrNull() ?: "") {
                    selectedAlertMode = listOf(it)
                }
            }

            // Repeat Alert (time) และ Select Repeat Alert (time)
            SettingsItem("Repeat Alert (time):", selectedRepeatAlertText) {
                visibleSelector = if (visibleSelector == VisibleSelector.REPEAT_ALERT) VisibleSelector.NONE else VisibleSelector.REPEAT_ALERT
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.REPEAT_ALERT) {
                SingleOptionSelector("Select Repeat Alert (time):", repeatAlertOptions, selectRepeatAlert.firstOrNull() ?: "") {
                    selectRepeatAlert = listOf(it)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Email Subscription", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            SettingsItem("Subscription Status:", "Not Subscribed", showIcon = false) {}


            SettingsItem("Notification Setting:", "custom", showIcon = false) {}
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


enum class VisibleSelector {
    NONE,
    ALERT_BEFORE_EXPIRED,
    ALERT_MODE,
    REPEAT_ALERT,
    ChangeMail
}

