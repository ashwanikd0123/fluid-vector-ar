package com.example.fluidvectorar.ui.editor.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fluidvectorar.AppRoute
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.domain.model.StrokeData
import com.example.fluidvectorar.ui.editor.components.EditorTopBar
import com.example.fluidvectorar.ui.editor.components.ExpandableBottomToolbar
import com.example.fluidvectorar.ui.editor.state.CanvasGestureState
import com.example.fluidvectorar.ui.editor.state.CanvasUIConfigState
import com.example.fluidvectorar.ui.editor.viewmodel.EditorScreenViewModel
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun EditorScreen(
    projectId: String,
    navController: NavController,
    viewModel: EditorScreenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadProject(projectId)
    }

    val editorState by viewModel.editorState.collectAsStateWithLifecycle()

    EditorScreenView(
        title = if (editorState.project != null) editorState.project!!.title else "Loading...",
        onBackClick = {
            navController.popBackStack(AppRoute.Home, inclusive = false)
        },
        onSaveClick = {
            TODO()
        },
        onUndoClick = {
            TODO()
        },
        onRedoClick = {
            TODO()
        },
        layers = editorState.layers,
        spitStroke = { strokeData ->
            viewModel.addStrokeToCurLayer(strokeData)
        }
    )
}


@Composable
fun EditorScreenView(
    title: String,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onUndoClick: () -> Unit = {},
    onRedoClick: () -> Unit = {},
    layers: List<LayerState> = emptyList(),
    spitStroke: (StrokeData) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        EditorTopBar(
            projectTitle = title,
            onBackClick = onBackClick,
            onUndoClick = { onUndoClick() },
            onRedoClick = { onRedoClick() },
            onSaveClick = onSaveClick
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            val canvasGestureState = remember { CanvasGestureState() }
            val canvasUIConfigState = remember { CanvasUIConfigState() }

            FluidCanvas(
                canvasState = canvasGestureState,
                modifier = Modifier.fillMaxSize(),
                layers = layers,
                activeMode = canvasUIConfigState.activeMode,
                isReticleEnabled = canvasUIConfigState.isReticleEnabled,
                isGridEnabled = canvasUIConfigState.isGridEnabled,
                gridSizeDp = canvasUIConfigState.gridSizeDp,
                currentBrushStyle = canvasUIConfigState.currentBrushStyle,
                spitStroke = { strokeData ->
                    spitStroke(strokeData)
                }
            )

            ExpandableBottomToolbar(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                canvasMode = canvasUIConfigState.activeMode,
                onClickCanvasModeChangeButton = { newCanvasMode ->
                    canvasUIConfigState.activeMode = newCanvasMode
                },
                currentBrushStyle = canvasUIConfigState.currentBrushStyle
            )
        }
    }
}

@Preview
@Composable
fun EditorScreenViewPreview() {
    FluidVectorARTheme {
        EditorScreenView(
            title = "Preview"
        )
    }
}