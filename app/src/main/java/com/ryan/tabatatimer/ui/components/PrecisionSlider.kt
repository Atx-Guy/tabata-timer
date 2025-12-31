package com.ryan.tabatatimer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PrecisionSlider(
    label: String,
    value: Int,
    range: IntRange,
    step: Int = 1,
    onValueChange: (Int) -> Unit,
    valueFormatter: (Int) -> String = { "$it" } // Default formatter
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Label Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valueFormatter(value),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Slider Control Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Decrease Button
            FilledTonalIconButton(
                onClick = {
                    val newValue = (value - step).coerceIn(range.first, range.last)
                    onValueChange(newValue)
                },
                enabled = value > range.first,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Decrease $label"
                )
            }

            // Slider
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.roundToInt()) },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                steps = ((range.endInclusive - range.start) / step) - 1,
                modifier = Modifier.weight(1f)
            )

            // Increase Button
            FilledTonalIconButton(
                onClick = {
                    val newValue = (value + step).coerceIn(range.first, range.last)
                    onValueChange(newValue)
                },
                enabled = value < range.last,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Increase $label"
                )
            }
        }
    }
}
