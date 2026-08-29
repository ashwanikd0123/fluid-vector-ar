package com.example.fluidvectorar.ui.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme
import java.util.UUID

@Composable
fun LayerManagementPanel(
    modifier: Modifier = Modifier,
    layers: List<LayerState>,
    activeLayerIndex: Int = 0,
    onAddLayer: (String) -> Unit = {}, // Updated to pass layer name
    onToggleVisibility: (String) -> Unit = {},
    onDeleteLayer: (String) -> Unit = {},
    onSelectLayer: (Int) -> Unit = {},
    onReorderLayers: (fromActualIndex: Int, toActualIndex: Int) -> Unit = { _, _ -> } // New Callback
) {
    // State for Inline Add Layer
    var isAddingLayer by remember { mutableStateOf(false) }
    var newLayerName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isAddingLayer) {
        if (isAddingLayer) {
            focusRequester.requestFocus()
        }
    }

    // State for Drag & Drop
    val listState = rememberLazyListState()
    var draggedItemId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    val currentLayers by rememberUpdatedState(layers)
    val currentOnReorder by rememberUpdatedState(onReorderLayers)

    // UI List (excluding background layer 0)
    val uiList = remember(layers) { layers.reversed().filter { layers.indexOf(it) != 0 } }
    val currentUiList by rememberUpdatedState(uiList)

    Card(
        modifier = modifier
            .width(320.dp)
            .heightIn(max = 500.dp)
            .imePadding()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // 1. Header
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

                IconButton(
                    onClick = { isAddingLayer = !isAddingLayer },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isAddingLayer) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Toggle Add Layer",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 2. Inline Add Layer Input (Animated)
            AnimatedVisibility(visible = isAddingLayer) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newLayerName,
                        onValueChange = { newLayerName = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .focusRequester(focusRequester),
                        placeholder = { Text("Layer Name", style = MaterialTheme.typography.bodySmall) },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            if (newLayerName.isNotBlank()) {
                                onAddLayer(newLayerName)
                                newLayerName = ""
                                isAddingLayer = false
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save Layer", tint = Color(0xFF22C55E))
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // 3. Layer List
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    items = uiList,
                    key = { _, layer -> layer.id }
                ) { _, layer ->
                    val actualIndex = layers.indexOfFirst { it.id == layer.id }
                    val isSelected = actualIndex == activeLayerIndex
                    val isDragged = draggedItemId == layer.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .zIndex(if (isDragged) 1f else 0f)
                            .graphicsLayer {
                                translationY = if (isDragged) dragOffset else 0f
                                scaleX = if (isDragged) 1.05f else 1f
                                scaleY = if (isDragged) 1.05f else 1f
                                shadowElevation = if (isDragged) 12f else 0f
                            }
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectLayer(actualIndex) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Drag Handle (Long press to reorder)
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Drag to reorder",
                            tint = Color.Gray,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .pointerInput(layer.id) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { _ ->
                                            draggedItemId = layer.id
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffset += dragAmount.y

                                            val currentDraggedIndex = currentUiList.indexOfFirst { it.id == draggedItemId }
                                            if (currentDraggedIndex != -1) {
                                                val layoutInfo = listState.layoutInfo
                                                val visibleItems = layoutInfo.visibleItemsInfo
                                                val draggedItemInfo = visibleItems.firstOrNull { it.key == draggedItemId }

                                                if (draggedItemInfo != null) {
                                                    val draggedItemCenter = draggedItemInfo.offset + draggedItemInfo.size / 2 + dragOffset.toInt()

                                                    val targetItem = visibleItems.firstOrNull { item ->
                                                        val itemCenter = item.offset + item.size / 2
                                                        if (dragOffset > 0) {
                                                            // Dragging down
                                                            item.index > draggedItemInfo.index && draggedItemCenter > itemCenter
                                                        } else {
                                                            // Dragging up
                                                            item.index < draggedItemInfo.index && draggedItemCenter < itemCenter
                                                        }
                                                    }

                                                    if (targetItem != null && targetItem.key != "spacer") {
                                                        val fromActualIndex = currentLayers.indexOfFirst { it.id == layer.id }
                                                        val targetLayerId = targetItem.key as String
                                                        val toActualIndex = currentLayers.indexOfFirst { it.id == targetLayerId }

                                                        if (fromActualIndex != -1 && toActualIndex != -1) {
                                                            currentOnReorder(fromActualIndex, toActualIndex)
                                                            // Adjust drag offset to keep the item under finger
                                                            dragOffset -= (targetItem.offset - draggedItemInfo.offset)
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        onDragEnd = {
                                            draggedItemId = null
                                            dragOffset = 0f
                                        },
                                        onDragCancel = {
                                            draggedItemId = null
                                            dragOffset = 0f
                                        }
                                    )
                                }
                        )


                        // Layer Name
                        Text(
                            text = layer.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.DarkGray
                        )

                        // Visibility Toggle
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
    val count = 5
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