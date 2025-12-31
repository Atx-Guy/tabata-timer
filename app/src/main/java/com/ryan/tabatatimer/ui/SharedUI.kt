package com.ryan.tabatatimer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.spacedBy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("My Tabata") }
    var work by remember { mutableStateOf(20f) }
    var rest by remember { mutableStateOf(10f) }
    var rounds by remember { mutableStateOf(8f) }
    var warmup by remember { mutableStateOf(5f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Workout") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                SliderInput("Work", work, 5f, 300f) { work = it }
                SliderInput("Rest", rest, 0f, 300f) { rest = it }
                SliderInput("Rounds", rounds, 1f, 50f) { rounds = it }
                SliderInput("Warmup", warmup, 0f, 60f) { warmup = it }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(name, work.toInt(), rest.toInt(), rounds.toInt(), warmup.toInt())
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

@Composable
fun SliderInput(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${value.toInt()}${if (label == "Rounds") "" else "s"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            steps = ((max - min) / (if (label == "Rounds") 1 else 5)).toInt() - 1
        )
    }
}