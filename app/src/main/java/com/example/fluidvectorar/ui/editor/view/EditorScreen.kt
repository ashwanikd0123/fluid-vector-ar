package com.example.fluidvectorar.ui.editor.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
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
import com.example.fluidvectorar.ui.editor.utils.saveImageAndCalculateCenter
import com.example.fluidvectorar.ui.editor.viewmodel.EditorScreenViewModel
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun EditorScreen(
    @Suppress("UNUSED_PARAMETER") navController: NavController,
    viewModel: EditorScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel.uiEvent, lifecycleOwner) {
        viewModel.uiEvent
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
    }

    val editorState by viewModel.editorState.collectAsStateWithLifecycle()


    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                viewModel.setImportingImage(true)

                val result = saveImageAndCalculateCenter(
                    context = context,
                    uri = uri,
                    canvasWidth = screenWidthPx,
                    canvasHeight = screenHeightPx
                )

                if (result != null) {
                    val (path, offset) = result
                    viewModel.addImageLayer(path, offset)
                }

                viewModel.setImportingImage(false)
            }
        }
    }

    EditorScreenView(
        onSaveClick = {
            viewModel.saveProject()
        },
        onImportImageClick = {
            photoPickerLauncher.launch("image/*")
        },
        onUndoClick = {
            viewModel.undo()
        },
        onRedoClick = {
            viewModel.redo()
        },
        spitStroke = { strokeData ->
            viewModel.addStrokeToCurLayer(strokeData)
        },
        layers = editorState.layers,
        activeLayerIndex = editorState.activeLayerIndex,
        updateActiveImageTransform = { centroid, pan, zoom, rotation ->
            viewModel.updateActiveImageTransform(pan, zoom, rotation)
        },
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
        },
        canvasWidth = editorState.project?.canvasWidth ?: 1080,
        canvasHeight = editorState.project?.canvasHeight ?: 1960,
        showCircularProgress = editorState.isLoadingProject || editorState.isSavingProject || editorState.isImportingImage
    )
}


@Composable
fun EditorScreenView(
    onSaveClick: () -> Unit = {},
    onImportImageClick: () -> Unit = {},
    onUndoClick: () -> Unit = {},
    onRedoClick: () -> Unit = {},
    spitStroke: (StrokeData) -> Unit = {},
    layers: List<LayerState> = emptyList(),
    activeLayerIndex: Int = 0,
    updateActiveImageTransform: (Offset, Offset, Float, Float) -> Unit = { _, _, _, _ -> },
    onAddLayer: (String) -> Unit = {}, // Updated to pass layer name
    onToggleVisibility: (String) -> Unit = {},
    onDeleteLayer: (String) -> Unit = {},
    onSelectLayer: (Int) -> Unit = {},
    onReorderLayers: (fromIndex: Int, toIndex: Int) -> Unit = { _, _ -> },
    canvasWidth: Int = 1080,
    canvasHeight: Int = 1960,
    showCircularProgress: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val canvasGestureState = remember { CanvasGestureState() }
        val canvasUIConfigState = remember { CanvasUIConfigState() }

        var activeDialog by remember { mutableStateOf(SettingDialogState.NONE) }

        FluidCanvas(
            modifier = Modifier.fillMaxSize(),
            canvasState = canvasGestureState,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight,
            layers = layers,
            activeLayerIndex = activeLayerIndex,
            updateActiveImageTransform = { centroid, pan, zoom, rotation ->
                updateActiveImageTransform(centroid, pan, zoom, rotation)
            },
            activeMode = canvasUIConfigState.activeMode,
            isReticleEnabled = canvasUIConfigState.isReticleEnabled,
            isGridEnabled = canvasUIConfigState.isGridEnabled,
            gridSizeDp = canvasUIConfigState.gridSizeDp,
            currentBrushStyle = canvasUIConfigState.currentBrushStyle,
            spitStroke = { strokeData ->
                spitStroke(strokeData)
            }
        )

        EditorTopBar(
            onUndoClick = { onUndoClick() },
            onRedoClick = { onRedoClick() },
            onSaveClick = onSaveClick,
            onImportImageClick = onImportImageClick,
            isReticleEnabled = canvasUIConfigState.isReticleEnabled,
            onToggleReticle = {
                canvasUIConfigState.isReticleEnabled = !canvasUIConfigState.isReticleEnabled
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .zIndex(1f)
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

        if (showCircularProgress) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .zIndex(10f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
        EditorScreenView()
    }
}