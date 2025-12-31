package com.ryan.tabatatimer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryan.tabatatimer.ui.theme.WorkColor

@Composable
fun CircularTimer(
    progress: Float,
    timeText: String,
    circleColor: Color = WorkColor,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Background Circle
        Canvas(modifier = Modifier.size(250.dp)) {
            drawCircle(
                color = Color.DarkGray,
                style = Stroke(width = 20.dp.toPx())
            )
        }
        
        // Progress Arc
        Canvas(modifier = Modifier.size(250.dp)) {
            drawArc(
                color = circleColor,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Time Text (Placeholder for now, will use styled text later)
        androidx.compose.material3.Text(text = timeText)
    }
}

@Preview
@Composable
fun PreviewCircularTimer() {
    CircularTimer(progress = 0.7f, timeText = "20")
}
