package com.example.fluidvectorar.ui.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left part: Save button with rounded background
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            IconButton(onClick = onSaveClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_save),
                    contentDescription = "Save",
                    tint = Color.DarkGray
                )
            }
        }

        // Right part: Undo & Redo with rounded background
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
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
fun EditorTopBarPreview() {
    FluidVectorARTheme {
        EditorTopBar(
            {},
            {},
            {}
        )
    }
}