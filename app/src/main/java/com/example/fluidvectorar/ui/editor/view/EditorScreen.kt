package com.example.fluidvectorar.ui.editor.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fluidvectorar.AppRoute
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.domain.model.StrokeData
import com.example.fluidvectorar.ui.editor.components.BrushSettingsPanel
import com.example.fluidvectorar.ui.editor.components.ColorPickerDialog
import com.example.fluidvectorar.ui.editor.components.EditorTopBar
import com.example.fluidvectorar.ui.editor.components.ExpandableBottomToolbar
import com.example.fluidvectorar.ui.editor.state.CanvasGestureState
import com.example.fluidvectorar.ui.editor.state.CanvasUIConfigState
import com.example.fluidvectorar.ui.editor.state.SettingDialogState
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

            var activeDialog by remember { mutableStateOf(SettingDialogState.NONE) }

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

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {

                SettingDialogs(
                    activeDialog = activeDialog,
                    canvasUIConfigState = canvasUIConfigState,
                    onActiveDialogChange = { activeDialog = it }
                )

                Spacer(
                    modifier = Modifier
                        .height(8.dp)
                )

                ExpandableBottomToolbar(
                    modifier = Modifier,
                    canvasMode = canvasUIConfigState.activeMode,
                    onClickCanvasModeChangeButton = { newCanvasMode ->
                        canvasUIConfigState.activeMode = newCanvasMode
                    },
                    currentBrushStyle = canvasUIConfigState.currentBrushStyle,
                    onColorClick = {
                        if (activeDialog == SettingDialogState.COLOR_SETTING) {
                            activeDialog = SettingDialogState.NONE
                            return@ExpandableBottomToolbar
                        }
                        activeDialog = SettingDialogState.COLOR_SETTING
                    },
                    onBrushSettingsClick = {
                        if (activeDialog == SettingDialogState.PENCIL_SETTING) {
                            activeDialog = SettingDialogState.NONE
                            return@ExpandableBottomToolbar
                        }
                        activeDialog = SettingDialogState.PENCIL_SETTING
                    },
                    onBrushStyleChange = { newBrushStyle ->
                        canvasUIConfigState.currentBrushStyle = newBrushStyle
                    },
                    onLayersClick = {
                        TODO()
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingDialogs(
    activeDialog: SettingDialogState,
    canvasUIConfigState: CanvasUIConfigState,
    onActiveDialogChange: (SettingDialogState) -> Unit
) {
    AnimatedContent(
        targetState = activeDialog,
        transitionSpec = {
            (scaleIn(
                transformOrigin = TransformOrigin(1f, 1f),
                animationSpec = tween(250)
            ) + fadeIn()).togetherWith(
                scaleOut(
                    transformOrigin = TransformOrigin(1f, 1f),
                    animationSpec = tween(200)
                ) + fadeOut()
            )
        },
        label = "setting_dialogs_transition",
        contentAlignment = Alignment.BottomEnd
    ) { targetDialog ->
        when (targetDialog) {
            SettingDialogState.COLOR_SETTING -> {
                ColorPickerDialog(
                    currentColorHex = canvasUIConfigState.currentBrushStyle.colorHex,
                    onColorSelected = { color ->
                        canvasUIConfigState.currentBrushStyle =
                            canvasUIConfigState.currentBrushStyle.copy(
                                colorHex = color
                            )
                        onActiveDialogChange(SettingDialogState.NONE)
                    }
                )
            }

            SettingDialogState.PENCIL_SETTING -> {
                BrushSettingsPanel(
                    currentStrokeWidth = canvasUIConfigState.currentBrushStyle.strokeWidth,
                    currentColorHex = canvasUIConfigState.currentBrushStyle.colorHex,
                    onStrokeWidthChanged = { newWidth ->
                        canvasUIConfigState.currentBrushStyle =
                            canvasUIConfigState.currentBrushStyle.copy(
                                strokeWidth = newWidth
                            )
                    },
                )
            }

            else -> {
                // Return empty content for states without dialogs in this panel
                Spacer(modifier = Modifier.size(0.dp))
            }
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