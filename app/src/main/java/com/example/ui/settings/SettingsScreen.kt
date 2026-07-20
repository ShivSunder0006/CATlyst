package com.example.ui.settings

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import com.example.ui.theme.bounceClick
import androidx.compose.material3.ripple

import com.example.ui.theme.AppSpacing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.ui.AppViewModel

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    val useSystemTheme by viewModel.useSystemTheme.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonArray by remember { mutableStateOf<JSONArray?>(null) }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val json = inputStream?.bufferedReader().use { reader -> reader?.readText() }
                if (json != null) {
                    restoreJsonArray = JSONArray(json)
                    showRestoreDialog = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                viewModel.showSnackbar("Invalid backup file.")
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore Data") },
            text = { Text("This will overwrite your existing data. Are you sure you want to proceed?") },
            confirmButton = {
                TextButton(onClick = {
                    restoreJsonArray?.let { jsonArray ->
                        val importedSessions = mutableListOf<com.example.data.Session>()
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            importedSessions.add(
                                com.example.data.Session(
                                    date = obj.getLong("date"),
                                    section = obj.getString("section"),
                                    questionsSolved = obj.getInt("questionsSolved"),
                                    goal = if (obj.has("goal") && !obj.isNull("goal")) obj.getInt("goal") else null
                                )
                            )
                        }
                        viewModel.restoreAllSessions(importedSessions)
                    }
                    showRestoreDialog = false
                    restoreJsonArray = null
                }) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.Medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                    Text("Daily Goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(AppSpacing.Medium))
                    
                    var goalText by remember(dailyGoal) { mutableStateOf(dailyGoal.toString()) }
                    OutlinedTextField(
                        value = goalText,
                        onValueChange = { 
                            goalText = it
                            val newGoal = it.toIntOrNull()
                            if (newGoal != null && newGoal > 0) {
                                viewModel.setDailyGoal(newGoal)
                            }
                        },
                        label = { Text("Questions per day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                    Text("Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(AppSpacing.Medium))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Follow system theme")
                        Switch(
                            checked = useSystemTheme,
                            onCheckedChange = { viewModel.setUseSystemTheme(it) }
                        )
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                    Text("Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(AppSpacing.Medium))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Haptic Feedback")
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = { viewModel.setHapticsEnabled(it) }
                        )
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                    Text("Data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(AppSpacing.Medium))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Backup Data")
                        Button(onClick = {
                            coroutineScope.launch {
                                try {
                                    val jsonArray = JSONArray()
                                    allSessions.forEach { session ->
                                        val obj = JSONObject()
                                        obj.put("date", session.date)
                                        obj.put("section", session.section)
                                        obj.put("questionsSolved", session.questionsSolved)
                                        obj.put("goal", session.goal)
                                        jsonArray.put(obj)
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_SUBJECT, "CATlyst Backup")
                                        putExtra(Intent.EXTRA_TEXT, jsonArray.toString())
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Backup Data"))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }) {
                            Text("Export")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(AppSpacing.Medium))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Restore Data")
                        Button(onClick = {
                            filePicker.launch("application/json")
                        }) {
                            Text("Import")
                        }
                    }
                }
            }
        }

        item {
            var showAboutDialog by remember { mutableStateOf(false) }
            if (showAboutDialog) {
                AboutDialog(onDismiss = { showAboutDialog = false })
            }
            val aboutInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(aboutInteractionSource, MaterialTheme.shapes.medium)
                    .clickable(
                        interactionSource = aboutInteractionSource,
                        indication = ripple(color = MaterialTheme.colorScheme.onSurface)
                    ) {
                        showAboutDialog = true
                    },
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                    Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(AppSpacing.Small))
                    Text("CATlyst - Minimalist CAT prep tracker", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
