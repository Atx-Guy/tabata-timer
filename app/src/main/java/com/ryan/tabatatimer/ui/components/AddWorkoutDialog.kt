package com.ryan.tabatatimer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ryan.tabatatimer.util.SoundManager

@Composable
fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, Int) -> Unit,
    soundManager: SoundManager // Passing to keep signature consistent with usage, though mostly strictly UI
) {
    var name by remember { mutableStateOf("New Workout") }
    var work by remember { mutableStateOf("20") }
    var rest by remember { mutableStateOf("10") }
    var rounds by remember { mutableStateOf("8") }
    var warmup by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Timer") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = work,
                    onValueChange = { work = it },
                    label = { Text("Work (sec)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rest,
                    onValueChange = { rest = it },
                    label = { Text("Rest (sec)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rounds,
                    onValueChange = { rounds = it },
                    label = { Text("Rounds") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = warmup,
                    onValueChange = { warmup = it },
                    label = { Text("Warmup (sec)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = work.toIntOrNull() ?: 20
                    val r = rest.toIntOrNull() ?: 10
                    val rnds = rounds.toIntOrNull() ?: 8
                    val wu = warmup.toIntOrNull() ?: 5
                    onSave(name, w, r, rnds, wu)
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
