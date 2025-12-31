package com.ryan.tabatatimer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.ryan.tabatatimer.ui.components.PrecisionSlider

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.collectAsState
import com.ryan.tabatatimer.ui.theme.MutedTeal
import com.ryan.tabatatimer.util.SoundManager

@Composable
fun WorkoutEditor(
    name: String,
    onNameChange: (String) -> Unit,
    work: Int,
    onWorkChange: (Int) -> Unit,
    rest: Int,
    onRestChange: (Int) -> Unit,
    rounds: Int,
    onRoundsChange: (Int) -> Unit,
    warmup: Int,
    onWarmupChange: (Int) -> Unit,
    isAudioEnabled: Boolean,
    onAudioEnabledChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit,
    isEditingConfig: Boolean, // True if editing existing, False if new
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
        PrecisionSlider(
            label = "Work",
            value = work,
            range = 5..300,
            onValueChange = onWorkChange,
            valueFormatter = { "${it}s" }
        )
        PrecisionSlider(
            label = "Rest",
            value = rest,
            range = 0..300,
            onValueChange = onRestChange,
            valueFormatter = { "${it}s" }
        )
        PrecisionSlider(
            label = "Rounds",
            value = rounds,
            range = 1..50,
            onValueChange = onRoundsChange,
            valueFormatter = { "$it" } 
        )
        PrecisionSlider(
            label = "Warmup",
            value = warmup,
            range = 0..60,
            onValueChange = onWarmupChange,
            valueFormatter = { "${it}s" }
        )
        
        // Audio Cues Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "Audio Cues",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = isAudioEnabled,
                onCheckedChange = onAudioEnabledChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MutedTeal,
                    checkedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditingConfig) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                   colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("CANCEL")
                }
            }
            
            androidx.compose.material3.Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (isEditingConfig) "UPDATE" else "SAVE")
            }
        }
    }
}