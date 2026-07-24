package com.example.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        
        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(Activity.RESULT_CANCELED, resultValue)
        
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WidgetConfigScreen(
                        appWidgetId = appWidgetId,
                        onConfigSaved = {
                            setResult(Activity.RESULT_OK, resultValue)
                            
                            // Send update broadcast
                            val updateIntent = Intent(this, DailyProgressWidgetReceiver::class.java).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                            }
                            sendBroadcast(updateIntent)
                            val updateIntent2 = Intent(this, QuickSessionWidgetReceiver::class.java).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                            }
                            sendBroadcast(updateIntent2)
                            val updateIntent3 = Intent(this, DashboardWidgetReceiver::class.java).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                            }
                            sendBroadcast(updateIntent3)
                            val updateIntent4 = Intent(this, QuickLaunchWidgetReceiver::class.java).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                            }
                            sendBroadcast(updateIntent4)
                            
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetConfigScreen(appWidgetId: Int, onConfigSaved: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { WidgetPreferences(context) }
    
    var theme by remember { mutableStateOf("Follow App") }
    var cornerRadius by remember { mutableStateOf(16) }
    var showLabels by remember { mutableStateOf(true) }
    var isCompact by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Widget Configuration", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Theme", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = theme == "Follow App", onClick = { theme = "Follow App" }, label = { Text("App") })
            FilterChip(selected = theme == "Light", onClick = { theme = "Light" }, label = { Text("Light") })
            FilterChip(selected = theme == "Dark", onClick = { theme = "Dark" }, label = { Text("Dark") })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Corner Radius: $cornerRadius dp", style = MaterialTheme.typography.titleMedium)
        Slider(value = cornerRadius.toFloat(), onValueChange = { cornerRadius = it.toInt() }, valueRange = 0f..32f)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = showLabels, onCheckedChange = { showLabels = it })
            Text("Show Labels")
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = isCompact, onCheckedChange = { isCompact = it })
            Text("Compact Layout")
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = {
                coroutineScope.launch {
                    prefs.saveConfig(appWidgetId, WidgetConfig(theme, cornerRadius, showLabels, isCompact))
                    onConfigSaved()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Text("Save Settings")
        }
    }
}
