package com.example.ui.stats

import com.example.ui.theme.AppSpacing

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.Session
import com.example.ui.theme.bounceClick
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionSheet(
    session: Session,
    onDismiss: () -> Unit,
    onSave: (Session) -> Unit,
    onDelete: (Session) -> Unit
) {
    var section by remember { mutableStateOf(session.section) }
    var count by remember { mutableStateOf(session.questionsSolved.toString()) }
    var goal by remember { mutableStateOf(session.goal?.toString() ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.Large)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Session", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = {
                    onDelete(session)
                    onDismiss()
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Session",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Text("Section", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small)
            ) {
                listOf("VARC", "LRDI", "Quant").forEach { s ->
                    FilterChip(
                        selected = section == s,
                        onClick = { section = s },
                        label = { Text(s) }
                    )
                }
            }

            OutlinedTextField(
                value = count,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) count = it },
                label = { Text("Questions Solved") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = goal,
                onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) goal = it },
                label = { Text("Target (Optional)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            val saveInteractionSource = remember { MutableInteractionSource() }
            Button(
                onClick = {
                    val countInt = count.toIntOrNull() ?: 0
                    if (countInt > 0) {
                        onSave(session.copy(section = section, questionsSolved = countInt, goal = goal.toIntOrNull()))
                        onDismiss()
                    }
                },
                interactionSource = saveInteractionSource,
                modifier = Modifier.fillMaxWidth().height(56.dp).bounceClick(saveInteractionSource, MaterialTheme.shapes.medium),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Save Changes")
            }
        }
    }
}
