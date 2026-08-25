package com.example.fluidvectorar.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fluidvectorar.ui.editor.canvas.state.CanvasState
import com.example.fluidvectorar.ui.editor.canvas.view.FluidCanvas
import com.example.fluidvectorar.ui.editor.components.EditorTopBar
import com.example.fluidvectorar.ui.editor.components.ExpandableBottomToolbar
import com.example.fluidvectorar.ui.editor.viewmodel.EditorScreenViewModel
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun EditorScreen(
    projectId: String,
    navController: NavController,
    viewModel: EditorScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {

    }
}


@Composable
fun EditorScreenView(
    canvasState: CanvasState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    // COLUMN used instead of Box to prevent TopBar from overlapping the Canvas
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {

        // 1. FIXED TOP ACTION BAR (Takes only its required height)
        EditorTopBar(
            projectTitle = "Untitled Vector",
            onBackClick = onBackClick,
            onUndoClick = { /* Undo */ },
            onRedoClick = { /* Redo */ },
            onSaveClick = onSaveClick
        )

        // 2. CANVAS AREA (Takes remaining 100% space)
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {

            FluidCanvas(
                canvasState = canvasState,
                modifier = Modifier.fillMaxSize()
            )

            // 3. COLLAPSIBLE FLOATING TOOLBAR
            ExpandableBottomToolbar(
                canvasState = canvasState,
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Bottom Right positioning
                    .padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun EditorScreenViewPreview() {
    val canvasState = CanvasState()
    FluidVectorARTheme {
        EditorScreenView(
            canvasState,
            {},
            {}
        )
    }
}