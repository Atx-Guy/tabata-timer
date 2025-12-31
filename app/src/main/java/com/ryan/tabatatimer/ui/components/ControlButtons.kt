package com.ryan.tabatatimer.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ControlButtons(
    isRunning: Boolean,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Button(onClick = onPlayPause) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Start"
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset")
        }
    }
}

@Preview
@Composable
fun PreviewControlButtons() {
    ControlButtons(isRunning = false, onPlayPause = {}, onReset = {})
}
