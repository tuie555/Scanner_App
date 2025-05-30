package com.LingTH.fridge.setting
import Databases.Settings
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.LingTH.fridge.MainActivity2
import com.LingTH.fridge.setting.components.SettingsItem
import com.LingTH.fridge.setting.components.SettingsTopBar
import com.LingTH.fridge.setting.components.SingleOptionSelector
import com.LingTH.fridge.ui.theme.getAdaptiveHorizontalPadding
import viewmodel.SettingsViewModel


@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel
) {

    var visibleSelector by remember { mutableStateOf(VisibleSelector.NONE) }
    var selectAlertbeforeEX by remember { mutableStateOf(listOf("3 days")) } // default: 3 days
    var selectedAlertMode by remember { mutableStateOf(listOf("Normal")) }   // default: Normal
    var selectRepeatAlert by remember { mutableStateOf(listOf("4")) }        // default: every 4 time
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val settingsState by viewModel.settings.collectAsState()
    BackHandler {
        val intent = Intent(context, MainActivity2::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)

        // Optional: ปิด Compose Activity ถ้าคุณไม่อยากให้กด back แล้วกลับมาอีก
        if (context is Activity) {
            context.finish()
        }
    }
    // โหลดค่าจาก Database ครั้งแรก
    LaunchedEffect(settingsState) {
        settingsState?.let {
            selectAlertbeforeEX = it.alertBeforeExpiry.split(", ")
            selectedAlertMode = listOf(it.alertMode)
            selectRepeatAlert = listOf(it.repeatAlert)
            email = it.email
        }
    }


    val selectedText = selectAlertbeforeEX.joinToString(", ")
    val selectedAlertModeText = selectedAlertMode.joinToString(", ")
    val selectedRepeatAlertText = selectRepeatAlert.joinToString(",")

    val alertOptions = listOf(
        "0 days","1 day", "2 days", "3 days", "4 days", "5 days", "1 week", "2 weeks",
        "3 weeks", "4 weeks", "1 month", "2 months", "3 months", "6 months"
    )
    val alertModeOptionsList = listOf("Normal", "E-Girlfriend", "Aggressive", "Friendly")
    val repeatAlertOptions = listOf("1", "2", "3", "4", "6", "7", "8")

    // Removed LocalConfiguration and related variables for padding

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(getAdaptiveHorizontalPadding()) // Use adaptive padding utility
            .verticalScroll(scrollState)
    ) {
        SettingsTopBar(navController)

        Column(modifier = Modifier.fillMaxWidth()) { // Changed from fillMaxSize to fillMaxWidth
            Text("Notification Setting", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            SettingsItem("Alert before expired:", selectedText) {
                visibleSelector = if (visibleSelector == VisibleSelector.ALERT_BEFORE_EXPIRED)
                    VisibleSelector.NONE else VisibleSelector.ALERT_BEFORE_EXPIRED
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.ALERT_BEFORE_EXPIRED) {
                SingleOptionSelector("Select Alert before expired:", alertOptions, selectAlertbeforeEX.firstOrNull() ?: "") {

                    selectAlertbeforeEX = it?.let { listOf(it) } ?: emptyList()}
            }

            SettingsItem("Alert Mode:", selectedAlertModeText) {
                visibleSelector = if (visibleSelector == VisibleSelector.ALERT_MODE)
                    VisibleSelector.NONE else VisibleSelector.ALERT_MODE
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.ALERT_MODE) {
                SingleOptionSelector("Select Alert Mode:", alertModeOptionsList, selectedAlertMode.firstOrNull() ?: "") {
                    selectedAlertMode = it?.let { listOf(it) } ?: emptyList()
                }
            }

            SettingsItem("Repeat Alert (time):", selectedRepeatAlertText) {
                visibleSelector = if (visibleSelector == VisibleSelector.REPEAT_ALERT)
                    VisibleSelector.NONE else VisibleSelector.REPEAT_ALERT
            }
            AnimatedVisibility(visible = visibleSelector == VisibleSelector.REPEAT_ALERT) {
                SingleOptionSelector("Select Repeat Alert (time):", repeatAlertOptions, selectRepeatAlert.firstOrNull() ?: "") {
                    selectRepeatAlert =it?.let { listOf(it) } ?: emptyList()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                val settings = Settings(
                    id = 0,
                    alertBeforeExpiry = selectAlertbeforeEX.joinToString(", "),
                    alertMode = selectedAlertMode.firstOrNull() ?: "",
                    repeatAlert = selectRepeatAlert.firstOrNull() ?: "",
                    email = email
                )

                viewModel.saveSettings(settings)
                Toast.makeText(context, "Save successful", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth() // Added fillMaxWidth
            ) {
                Text("Save Settings")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



enum class VisibleSelector {
    NONE,
    ALERT_BEFORE_EXPIRED,
    ALERT_MODE,
    REPEAT_ALERT,
}

