package com.example.fluidvectorar.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun ColorPickerDialog(
    modifier: Modifier = Modifier,
    currentColorHex: Long = Color.Blue.toColorLong(),
    onColorSelected: (Long) -> Unit = {},
) {
    val colorPalette = listOf(
        0xFF000000, 0xFFFFFFFF, 0xFFEF4444, 0xFFF97316,
        0xFFEAB308, 0xFF22C55E, 0xFF3B82F6, 0xFF8B5CF6,
        0xFFEC4899, 0xFF64748B
    )

    Card(
        // Width restrict kar di taaki poori screen na ghere
        modifier = modifier.width(220.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(colorPalette) { colorHex ->
                val isSelected = colorHex == currentColorHex
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(colorHex))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(colorHex) }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ColorPickerDialogPreview() {
    FluidVectorARTheme {
        ColorPickerDialog()
    }
}