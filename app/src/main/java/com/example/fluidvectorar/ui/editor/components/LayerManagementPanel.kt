package com.example.fluidvectorar.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme
import java.util.UUID

@Composable
fun LayerManagementPanel(
    modifier: Modifier = Modifier,
    layers: List<LayerState>,
    activeLayerIndex: Int = 0,
    onAddLayer: () -> Unit = {},
    onToggleVisibility: (String) -> Unit = {},
    onDeleteLayer: (String) -> Unit = {},
    onSelectLayer: (Int) -> Unit = {},
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .heightIn(max = 400.dp), // Height limit taaki badi list hone par scroll ho
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // 1. Header: Title, Add Layer (+), Close (X)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Layers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Row {
                    IconButton(onClick = onAddLayer, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add Layer", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // 2. Layer List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Reverse iterate taaki nayi layer upar dikhe (Photoshop style)
                itemsIndexed(layers.reversed()) { reversedIndex, layer ->
                    // Original array index nikalna zaroori hai kyunki list reversed hai
                    val actualIndex = layers.lastIndex - reversedIndex
                    val isSelected = actualIndex == activeLayerIndex

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectLayer(actualIndex) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Layer Name
                        Text(
                            text = layer.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.DarkGray
                        )

                        // Visibility Toggle (Eye icon)
                        IconButton(
                            onClick = { onToggleVisibility(layer.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (layer.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility",
                                tint = if (layer.isVisible) Color.DarkGray else Color.LightGray
                            )
                        }

                        // Delete Layer (Disable for Layer 0)
                        if (actualIndex > 0) {
                            IconButton(
                                onClick = { onDeleteLayer(layer.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Layer",
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            }
                        } else {
                            // Layer 0 ke liye empty space taaki UI align rahe
                            Spacer(modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LayerManagementPanelPreview() {
    val count = 10
    val layers = mutableListOf<LayerState>()
    for (i in 1..count) {
        layers.add(
            LayerState(
                id = UUID.randomUUID().toString(),
                name = "Layer ${i}"
            )
        )
    }

    FluidVectorARTheme {
        LayerManagementPanel(
            layers = layers,
            activeLayerIndex = 7
        )
    }
}