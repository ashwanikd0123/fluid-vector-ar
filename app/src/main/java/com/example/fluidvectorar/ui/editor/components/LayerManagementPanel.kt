package com.example.fluidvectorar.ui.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    onUpdateOpacity: (String, Float) -> Unit = { _, _ -> },
    onSelectLayer: (Int) -> Unit = {},
    onReorderLayers: (fromActualIndex: Int, toActualIndex: Int) -> Unit = { _, _ -> } // New Callback
) {
    // State for Inline Add Layer
    var isAddingLayer by remember { mutableStateOf(false) }
    var newLayerName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // State for Layer Settings
    var settingsLayerId by remember { mutableStateOf<String?>(null) }

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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(
                    items = uiList,
                    key = { _, layer -> layer.id }
                ) { _, layer ->
                    val actualIndex = layers.indexOfFirst { it.id == layer.id }
                    val isSelected = actualIndex == activeLayerIndex
                    val isDragged = draggedItemId == layer.id

                    val scale by animateFloatAsState(if (isDragged) 1.08f else 1f, label = "drag_scale")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (isDragged) Modifier else Modifier.animateItem())
                            .zIndex(if (isDragged) 1.5f else 0f)
                    ) {
                        // 1. Hover/Hint Slot (The "empty" space where the item would land)
                        if (isDragged) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .padding(vertical = 2.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            )
                        }

                        // 2. The Actual Layer Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationY = if (isDragged) dragOffset else 0f
                                    scaleX = scale
                                    scaleY = scale
                                    shadowElevation = if (isDragged) 12f else 0f
                                }
                                .background(
                                    color = when {
                                        isDragged -> Color.White
                                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                                        else -> Color.Transparent
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .then(
                                    if (isDragged) {
                                        Modifier.border(
                                            width = 1.dp,
                                            brush = SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    } else Modifier
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

                                                val currentDraggedItemId =
                                                    draggedItemId ?: return@detectDragGesturesAfterLongPress
                                                val layoutInfo = listState.layoutInfo
                                                val visibleItems = layoutInfo.visibleItemsInfo
                                                val draggedItemInfo =
                                                    visibleItems.firstOrNull { it.key == currentDraggedItemId }

                                                if (draggedItemInfo != null) {
                                                    val draggedItemCenter =
                                                        draggedItemInfo.offset + draggedItemInfo.size / 2 + dragOffset.toInt()

                                                    val targetItem = visibleItems.firstOrNull { item ->
                                                        val itemCenter = item.offset + item.size / 2
                                                        if (dragOffset > 0) {
                                                            // Dragging down: item must be below current and dragged center must pass its center
                                                            item.index > draggedItemInfo.index && draggedItemCenter > itemCenter
                                                        } else if (dragOffset < 0) {
                                                            // Dragging up: item must be above current and dragged center must pass its center
                                                            item.index < draggedItemInfo.index && draggedItemCenter < itemCenter
                                                        } else {
                                                            false
                                                        }
                                                    }

                                                    if (targetItem != null && targetItem.key != "spacer") {
                                                        val fromActualIndex =
                                                            currentLayers.indexOfFirst { it.id == currentDraggedItemId }
                                                        val targetLayerId = targetItem.key as String
                                                        val toActualIndex =
                                                            currentLayers.indexOfFirst { it.id == targetLayerId }

                                                        if (fromActualIndex != -1 && toActualIndex != -1) {
                                                            // Adjust drag offset BEFORE triggering reorder to avoid frames with wrong translation
                                                            val offsetDelta =
                                                                (targetItem.offset - draggedItemInfo.offset).toFloat()
                                                            dragOffset -= offsetDelta

                                                            currentOnReorder(fromActualIndex, toActualIndex)
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

                            // Gear Icon for Settings
                            IconButton(
                                onClick = { settingsLayerId = layer.id },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Layer Settings",
                                    tint = if (settingsLayerId == layer.id) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // 4. Layer Settings "Bottom Sheet" (Internal to Dialog)
            AnimatedVisibility(visible = settingsLayerId != null) {
                val layer = layers.find { it.id == settingsLayerId }
                if (layer != null) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Settings: ${layer.name}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            IconButton(
                                onClick = { settingsLayerId = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close Settings")
                            }
                        }

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .fillMaxWidth()
                        ) {
                            // Visibility Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Visible", style = MaterialTheme.typography.bodySmall)
                                Switch(
                                    checked = layer.isVisible,
                                    onCheckedChange = { onToggleVisibility(layer.id) },
                                    modifier = Modifier.graphicsLayer(scaleX = 0.7f, scaleY = 0.7f)
                                )
                            }

                            // Opacity Row
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Opacity", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        "${(layer.opacity * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Slider(
                                    value = layer.opacity,
                                    onValueChange = { onUpdateOpacity(layer.id, it) },
                                    valueRange = 0f..1f,
                                    modifier = Modifier.height(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Delete Action (Disable for Layer 0 if applicable)
                            if (layers.indexOf(layer) > 0) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDeleteLayer(layer.id)
                                            settingsLayerId = null
                                        }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Delete Layer", color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LayerManagementPanelPreview() {
    var layers by remember {
        mutableStateOf(
            listOf(
                LayerState(id = "0", name = "Background (Layer 0)", isVisible = true),
                LayerState(id = "1", name = "Pencil Sketch", isVisible = true),
                LayerState(id = "2", name = "Main Outline", isVisible = true),
                LayerState(id = "3", name = "Base Color", isVisible = true),
                LayerState(id = "4", name = "Lighting Details", isVisible = true)
            )
        )
    }
    var activeIndex by remember { mutableStateOf(1) }

    FluidVectorARTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            LayerManagementPanel(
                layers = layers,
                activeLayerIndex = activeIndex,
                onAddLayer = { name ->
                    layers = layers + LayerState(id = UUID.randomUUID().toString(), name = name)
                },
                onSelectLayer = { activeIndex = it },
                onDeleteLayer = { id ->
                    layers = layers.filter { it.id != id }
                },
                onToggleVisibility = { id ->
                    layers = layers.map {
                        if (it.id == id) it.copy(isVisible = !it.isVisible) else it
                    }
                },
                onUpdateOpacity = { id, opacity ->
                    layers = layers.map {
                        if (it.id == id) it.copy(opacity = opacity) else it
                    }
                },
                onReorderLayers = { from, to ->
                    val mutable = layers.toMutableList()
                    val item = mutable.removeAt(from)
                    mutable.add(to, item)
                    layers = mutable
                }
            )
        }
    }
}
