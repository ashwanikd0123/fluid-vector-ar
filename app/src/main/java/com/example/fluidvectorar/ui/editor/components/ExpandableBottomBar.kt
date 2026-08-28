package com.example.fluidvectorar.ui.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.R
import com.example.fluidvectorar.domain.model.BrushStyle
import com.example.fluidvectorar.domain.model.BrushType
import com.example.fluidvectorar.ui.editor.state.CanvasGestureState
import com.example.fluidvectorar.ui.editor.state.CanvasMode
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun ExpandableBottomToolbar(
    modifier: Modifier = Modifier,
    canvasMode: CanvasMode = CanvasMode.DRAW,
    onClickCanvasModeChangeButton: (CanvasMode) -> Unit = {},
    onColorClick: () -> Unit = {},
    onBrushSettingsClick: () -> Unit = {},
    onLayersClick: () -> Unit = {},
    onBrushStyleChange: (BrushStyle) -> Unit = {},
    currentBrushStyle: BrushStyle = BrushStyle(colorHex = 0xFF000000, strokeWidth = 8f),
    onExpandedStateChange: (Boolean) -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(true) }

    Box(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = if (isExpanded) RoundedCornerShape(32.dp) else CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(if (isExpanded) 12.dp else 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. EXPANDED TOOLS (Color, Brush, Eraser, Layers)
                AnimatedVisibility(visible = isExpanded) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        // Color Picker
                        IconButton(onClick = onColorClick) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(currentBrushStyle.colorHex))
                            )
                        }

                        // Brush / Eraser Toggle
                        IconButton(onClick = {
                            val newType = if (currentBrushStyle.brushType == BrushType.ERASER) {
                                BrushType.PENCIL
                            } else {
                                BrushType.ERASER
                            }
                            onBrushStyleChange(currentBrushStyle.copy(brushType = newType))
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (currentBrushStyle.brushType == BrushType.ERASER) {
                                        R.drawable.ic_eraser_mode
                                    } else {
                                        R.drawable.ic_pencil
                                    }
                                ),
                                contentDescription = "Toggle Pencil/Eraser",
                                tint = if (currentBrushStyle.brushType == BrushType.ERASER) MaterialTheme.colorScheme.primary else Color.DarkGray
                            )
                        }

                        // Brush / Stroke Settings
                        IconButton(onClick = onBrushSettingsClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_brush_settings),
                                contentDescription = "Brush Settings"
                            )
                        }

                        // Layers Management
                        IconButton(onClick = onLayersClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Layers") // Use a better layer icon if available
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color.LightGray))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                // 2. ALWAYS VISIBLE TOOLS (Pan Toggle + Expand/Collapse)

                // Pan/Draw Mode Switcher (Now outside AnimatedVisibility)
                val isDraw = canvasMode == CanvasMode.DRAW
                IconButton(
                    onClick = {
                        onClickCanvasModeChangeButton(if (isDraw) CanvasMode.PAN_ZOOM else CanvasMode.DRAW)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isDraw) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
                    )
                ) {
                    Icon(
                        imageVector = if (isDraw) Icons.Default.Create else Icons.Default.Search,
                        contentDescription = "Toggle Mode",
                        tint = if (isDraw) Color.White else Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Expand/Collapse Toggle
                IconButton(
                    onClick = {
                        isExpanded = !isExpanded
                        onExpandedStateChange(isExpanded)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Build,
                        contentDescription = if (isExpanded) "Collapse Toolbar" else "Expand Toolbar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ExpandableBottomToolbarPreview() {
    FluidVectorARTheme {
        ExpandableBottomToolbar()
    }
}