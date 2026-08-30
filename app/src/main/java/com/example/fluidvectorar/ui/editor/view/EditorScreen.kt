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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.zIndex
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
import com.example.fluidvectorar.ui.editor.components.LayerManagementPanel
import com.example.fluidvectorar.ui.editor.state.CanvasGestureState
import com.example.fluidvectorar.ui.editor.state.CanvasUIConfigState
import com.example.fluidvectorar.ui.editor.state.SettingDialogState
import com.example.fluidvectorar.ui.editor.viewmodel.EditorScreenViewModel
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme



@Composable
fun EditorScreen(
    navController: NavController,
    viewModel: EditorScreenViewModel = hiltViewModel()
) {

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
        spitStroke = { strokeData ->
            viewModel.addStrokeToCurLayer(strokeData)
        },
        layers = editorState.layers,
        activeLayerIndex = editorState.activeLayerIndex,
        onAddLayer = { layerName ->
            viewModel.addNewLayer(layerName)
        },
        onToggleVisibility = { layerId ->
            viewModel.toggleVisibility(layerId)
        },
        onDeleteLayer = { layerId ->
            viewModel.deleteLayer(layerId)
        },
        onSelectLayer = { layerId ->
            viewModel.setSelectedLayer(layerId)
        },
        onReorderLayers = { fromIndex, toIndex ->
            viewModel.reorderLayers(fromIndex, toIndex)
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
    spitStroke: (StrokeData) -> Unit = {},
    layers: List<LayerState> = emptyList(),
    activeLayerIndex: Int = 0,
    onAddLayer: (String) -> Unit = {}, // Updated to pass layer name
    onToggleVisibility: (String) -> Unit = {},
    onDeleteLayer: (String) -> Unit = {},
    onSelectLayer: (Int) -> Unit = {},
    onReorderLayers: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
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
            onSaveClick = onSaveClick,
            modifier = Modifier
                .statusBarsPadding()
                .zIndex(1f)
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
                    .navigationBarsPadding()
                    .zIndex(2f),
                horizontalAlignment = Alignment.End
            ) {

                SettingDialogs(
                    activeDialog = activeDialog,
                    canvasUIConfigState = canvasUIConfigState,
                    onActiveDialogChange = { activeDialog = it },
                    layers = layers,
                    activeLayerIndex = activeLayerIndex,
                    onAddLayer = { layerName ->
                        onAddLayer(layerName)
                    },
                    onToggleVisibility = { layerId ->
                        onToggleVisibility(layerId)
                    },
                    onDeleteLayer = { layerId ->
                        onDeleteLayer(layerId)
                    },
                    onSelectLayer = { layerId ->
                        onSelectLayer(layerId)
                    },
                    onReorderLayers = { fromIndex, toIndex ->
                        onReorderLayers(fromIndex, toIndex)
                    }
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
                    onLayersClick = {
                        if (activeDialog == SettingDialogState.LAYER_SETTING) {
                            activeDialog = SettingDialogState.NONE
                            return@ExpandableBottomToolbar
                        }
                        activeDialog = SettingDialogState.LAYER_SETTING
                    },
                    onBrushStyleChange = { newBrushStyle ->
                        activeDialog = SettingDialogState.NONE
                        canvasUIConfigState.currentBrushStyle = newBrushStyle
                    },
                    onExpandedStateChange = {
                        activeDialog = SettingDialogState.NONE
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
    onActiveDialogChange: (SettingDialogState) -> Unit,
    layers: List<LayerState> = emptyList(),
    activeLayerIndex: Int = 0,
    onAddLayer: (String) -> Unit = {}, // Updated to pass layer name
    onToggleVisibility: (String) -> Unit = {},
    onDeleteLayer: (String) -> Unit = {},
    onSelectLayer: (Int) -> Unit = {},
    onReorderLayers: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> }
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
        Box(modifier = Modifier.padding(16.dp)) {
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

                SettingDialogState.LAYER_SETTING -> {
                    LayerManagementPanel(
                        layers = layers,
                        activeLayerIndex = activeLayerIndex,
                        onAddLayer = { layerName ->
                            onAddLayer(layerName)
                        },
                        onToggleVisibility = { layerId ->
                            onToggleVisibility(layerId)
                        },
                        onDeleteLayer = { layerId ->
                            onDeleteLayer(layerId)
                        },
                        onSelectLayer = { layerId ->
                            onSelectLayer(layerId)
                        },
                        onReorderLayers = { fromIndex, toIndex ->
                            onReorderLayers(fromIndex, toIndex)
                        }
                    )
                }

                else -> {
                    // Return empty content for states without dialogs in this panel
                    Spacer(modifier = Modifier.size(0.dp))
                }
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