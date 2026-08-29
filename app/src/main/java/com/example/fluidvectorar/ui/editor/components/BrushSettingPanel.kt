package com.example.fluidvectorar.ui.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme
import kotlin.math.roundToInt

@Composable
fun BrushSettingsPanel(
    modifier: Modifier = Modifier,
    currentStrokeWidth: Float = 50f,
    currentColorHex: Long = 0xff000000,
    onStrokeWidthChanged: (Float) -> Unit = {},
) {
    Card(
        modifier = modifier.width(240.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Live Brush Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color(currentColorHex),
                        radius = currentStrokeWidth / 2f
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Thickness Slider
            Slider(
                value = currentStrokeWidth,
                onValueChange = onStrokeWidthChanged,
                valueRange = 1f..100f,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${currentStrokeWidth.roundToInt()} px",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BrushSettingPreview() {
    FluidVectorARTheme {
        BrushSettingsPanel()
    }
}