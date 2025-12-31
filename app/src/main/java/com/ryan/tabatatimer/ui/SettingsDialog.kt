package com.ryan.tabatatimer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ryan.tabatatimer.model.TimerConfig

@Composable
fun SettingsDialog(
    currentConfig: TimerConfig,
    onDismiss: () -> Unit,
    onConfirm: (TimerConfig) -> Unit
) {
    var prepareTime by remember { mutableStateOf(currentConfig.prepareTimeSeconds.toString()) }
    var workTime by remember { mutableStateOf(currentConfig.workTimeSeconds.toString()) }
    var restTime by remember { mutableStateOf(currentConfig.restTimeSeconds.toString()) }
    var rounds by remember { mutableStateOf(currentConfig.totalRounds.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Timer Settings") },
        text = {
            Column {
                OutlinedTextField(
                    value = prepareTime,
                    onValueChange = { prepareTime = it },
                    label = { Text("Prepare Time (s)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = workTime,
                    onValueChange = { workTime = it },
                    label = { Text("Work Time (s)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = restTime,
                    onValueChange = { restTime = it },
                    label = { Text("Rest Time (s)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rounds,
                    onValueChange = { rounds = it },
                    label = { Text("Rounds") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newConfig = currentConfig.copy(
                    prepareTimeSeconds = prepareTime.toIntOrNull() ?: currentConfig.prepareTimeSeconds,
                    workTimeSeconds = workTime.toIntOrNull() ?: currentConfig.workTimeSeconds,
                    restTimeSeconds = restTime.toIntOrNull() ?: currentConfig.restTimeSeconds,
                    totalRounds = rounds.toIntOrNull() ?: currentConfig.totalRounds
                )
                onConfirm(newConfig)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
