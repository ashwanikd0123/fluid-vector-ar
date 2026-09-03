package com.example.fluidvectorar.ui.editor.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.R
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun EditorTopBar(
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onImportImageClick: () -> Unit,
    isReticleEnabled: Boolean,
    onToggleReticle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left part: Save & Import dropdown
        var expanded by remember { mutableStateOf(false) }
        val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "arrow_rotation")

        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box {
                Row(
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Menu",
                        tint = Color.DarkGray
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand menu",
                        tint = Color.DarkGray,
                        modifier = Modifier.rotate(rotationState)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Save") },
                        onClick = {
                            expanded = false
                            onSaveClick()
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = null,
                                tint = Color.DarkGray
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Import Image") },
                        onClick = {
                            expanded = false
                            onImportImageClick()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.DarkGray
                            )
                        }
                    )
                }
            }
        }

        // Right part: Undo, Redo & Reticle with rounded background
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onUndoClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_undo),
                        contentDescription = "Undo",
                        tint = Color.DarkGray
                    )
                }
                IconButton(onClick = onRedoClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_redo),
                        contentDescription = "Redo",
                        tint = Color.DarkGray
                    )
                }
                IconButton(onClick = onToggleReticle) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_reticle),
                        contentDescription = "Toggle Reticle",
                        tint = if (isReticleEnabled) Color.Red else Color.LightGray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
fun EditorTopBarPreview() {
    FluidVectorARTheme {
        EditorTopBar(
            onUndoClick = {},
            onRedoClick = {},
            onSaveClick = {},
            onImportImageClick = {},
            isReticleEnabled = true,
            onToggleReticle = {}
        )
    }
}