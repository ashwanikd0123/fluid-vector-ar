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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.ui.editor.canvas.state.CanvasState
import com.example.fluidvectorar.ui.editor.canvas.state.CanvasMode
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun ExpandableBottomToolbar(
    canvasState: CanvasState,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            // animateContentSize se width/height circle se pill me smoothly animate hogi
            .animateContentSize(animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )),
        shape = if (isExpanded) RoundedCornerShape(32.dp) else CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(if (isExpanded) 12.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Expanded Mode Tools (Visible only when expanded)
            AnimatedVisibility(visible = isExpanded) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Tool 1: Color/Brush Picker
                    IconButton(onClick = { /* Open Color Picker Dialog */ }) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(canvasState.currentBrushStyle.colorHex))
                        )
                    }

                    // Tool 2: Pan/Draw Mode Switcher
                    val isDraw = canvasState.activeMode == CanvasMode.DRAW
                    IconButton(
                        onClick = { canvasState.activeMode = if (isDraw) CanvasMode.PAN_ZOOM else CanvasMode.DRAW },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isDraw) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
                        )
                    ) {
                        Icon(
                            imageVector = if (isDraw) Icons.Default.Create else Icons.Default.Search, // Create vs Pan Icon
                            contentDescription = "Toggle Mode",
                            tint = if (isDraw) Color.White else Color.DarkGray
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Divider
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color.LightGray))

                    Spacer(modifier = Modifier.width(8.dp))
                }
            }

            // The Main Toggle Button (Always visible)
            // Changes icon from "Brush" to "Close (X)" based on state
            IconButton(
                onClick = { isExpanded = !isExpanded },
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

@Preview
@Composable
fun ExpandableBottomToolbarPreview() {
    val canvasState = CanvasState()
    FluidVectorARTheme {
        ExpandableBottomToolbar(
            canvasState
        )
    }
}