package com.example.fluidvectorar.ui.editor.viewmodel


import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.fluidvectorar.AppRoute
import com.example.fluidvectorar.data.repository.DrawingRepository
import com.example.fluidvectorar.data.repository.StorageRepository
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.domain.model.StrokeData
import com.example.fluidvectorar.domain.model.toLayerEntity
import com.example.fluidvectorar.domain.model.toLayerState
import com.example.fluidvectorar.ui.editor.state.CanvasAction
import com.example.fluidvectorar.ui.editor.state.EditorUiState
import com.example.fluidvectorar.ui.editor.state.UndoRedoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditorScreenViewModel @Inject constructor(
    val drawingRepo: DrawingRepository,
    val storageRepo: StorageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val editorState = MutableStateFlow(EditorUiState())

    private val route: AppRoute.EditorStudio = savedStateHandle.toRoute()
    private val projectId: String = route.projectId ?: "unknown"

    private val undoRedoManager: UndoRedoManager = UndoRedoManager()

    init {
        loadProject(projectId)
    }

    fun saveProject() {
        val currentProject = editorState.value.project ?: return
        val currentLayers = editorState.value.layers

        viewModelScope.launch(Dispatchers.IO) {
            setSaving(true)
            try {
                val updatedProject = currentProject.copy(updatedAt = System.currentTimeMillis())
                drawingRepo.addProject(updatedProject)

                drawingRepo.deleteAllLayersInProject(updatedProject.id)

                currentLayers.forEachIndexed { index, layerState ->
                    val layerEntity = layerState.toLayerEntity(
                        projectId = updatedProject.id,
                        layerIndex = index,
                        strokesJsonPath = storageRepo.getLayerFilePath(updatedProject.id, layerState.id)
                    )
                    drawingRepo.addOrUpdateLayer(layerEntity)
                    storageRepo.saveStrokesToFile(updatedProject.id, layerState.id, layerState.strokes)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                setSaving(false)
            }
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {

            setLoading(true)

            val project = withContext(Dispatchers.IO) {
                drawingRepo.getProject(projectId)
            }

            editorState.update {
                it.copy(
                    project = project
                )
            }

            val layerEntities = withContext(Dispatchers.IO) {
                drawingRepo.getAllLayersInProject(project.id)
            }

            val layers = withContext(Dispatchers.IO) {
                layerEntities.map { layerE ->
                    val strokes = storageRepo.readStrokesFromLayer(project.id, layerE.id)
                    layerE.toLayerState(strokes)
                }
            }

            editorState.update {
                it.copy(
                    layers = layers,
                    activeLayerIndex = 0
                )
            }

            setLoading(false)
        }
    }

    fun setLoading(isLoading: Boolean) {
        editorState.update { it.copy(isLoadingProject = isLoading) }
    }

    fun setSaving(isSaving: Boolean) {
        editorState.update { it.copy(isSavingProject = isSaving) }
    }

    fun setImportingImage(isImporting: Boolean) {
        editorState.update { it.copy(isImportingImage = isImporting) }
    }

    fun addImageLayer(imagePath: String, centerOffset: Offset) {
        editorState.update { oldState ->
            val newLayer = LayerState(
                id = UUID.randomUUID().toString(),
                name = "Image Layer",
                imagePath = imagePath,
                imageOffset = centerOffset,
                imageScale = 1f,
                imageRotation = 0f
            )

            val mutableLayers = oldState.layers.toMutableList()
            val insertIndex = (oldState.activeLayerIndex + 1).coerceAtMost(mutableLayers.size)
            mutableLayers.add(insertIndex, newLayer)

            oldState.copy(
                layers = mutableLayers.toList(),
                activeLayerIndex = insertIndex
            )
        }
    }

    fun updateActiveImageTransform(pan: Offset, zoom: Float, rotationDelta: Float) {
        editorState.update { oldState ->
            val activeIndex = oldState.activeLayerIndex
            val activeLayer = oldState.layers.getOrNull(activeIndex)

            if (activeLayer?.imagePath == null) return@update oldState

            val mutableLayers = oldState.layers.toMutableList()
            mutableLayers[activeIndex] = activeLayer.copy(
                imageOffset = activeLayer.imageOffset + pan,
                imageScale = activeLayer.imageScale * zoom,
                imageRotation = activeLayer.imageRotation + rotationDelta
            )

            oldState.copy(layers = mutableLayers.toList())
        }
    }

    fun updateCanvasSize(width: Int, height: Int) {
        editorState.update { oldState ->
            oldState.copy(
                project = oldState.project?.copy(
                    canvasWidth = width,
                    canvasHeight = height
                )
            )
        }
    }

    fun addStrokeToCurLayer(strokeData: StrokeData) {
        undoRedoManager.addAction(
            CanvasAction.CommitStroke(
                layerId = editorState.value.activeLayer?.id ?: "unknown_layer",
                stroke = strokeData
            )
        )

        editorState.update { oldState ->
            oldState.copy(
                layers = oldState.layers.map { layerState ->
                    layerState.copy(
                        strokes =
                            if (oldState.activeLayer == layerState)
                                layerState.strokes + strokeData
                            else
                                layerState.strokes
                    )
                }
            )
        }
    }

    fun undo() {
        val canvasAction = undoRedoManager.popUndo() ?: return
        processAction(canvasAction, isUndo = true)
    }

    fun redo() {
        val canvasAction = undoRedoManager.popRedo() ?: return
        processAction(canvasAction, isUndo = false)
    }

    fun addNewLayer(layerName: String) {
        editorState.update { oldState ->
            oldState.copy(
                layers = oldState.layers + LayerState(
                    id = UUID.randomUUID().toString(),
                    name = layerName,
                ),
                activeLayerIndex = oldState.layers.count()
            )
        }
    }

    fun deleteLayer(layerId: String) {
        editorState.update { oldState ->
            val deletedIndex = oldState.layers.indexOfFirst { it.id == layerId }

            if (deletedIndex <= 0) return@update oldState

            val updatedLayers = oldState.layers.filterIndexed { index, _ -> index != deletedIndex }

            var newActiveIndex = oldState.activeLayerIndex

            if (deletedIndex <= oldState.activeLayerIndex) {
                newActiveIndex--
            }

            newActiveIndex = if (updatedLayers.size > 1) {
                newActiveIndex.coerceIn(1, updatedLayers.lastIndex)
            } else {
                0
            }

            oldState.copy(
                layers = updatedLayers,
                activeLayerIndex = newActiveIndex
            )
        }
    }

    fun setSelectedLayer(layerId: String) {
        editorState.update { oldState ->
            oldState.copy(
                activeLayerIndex = oldState.layers.indexOfFirst { layer ->
                    layer.id == layerId
                }
            )
        }
    }

    fun setSelectedLayer(layerIdx: Int) {
        editorState.update { oldState ->
            oldState.copy(
                activeLayerIndex = layerIdx.coerceIn(0, oldState.layers.count() - 1)
            )
        }
    }

    fun toggleVisibility(layerId: String) {
        editorState.update { oldState ->
            oldState.copy(
                layers = oldState.layers.map { layer ->
                    layer.copy(
                        isVisible = if (layer.id == layerId) !layer.isVisible else layer.isVisible
                    )
                }
            )
        }
    }

    fun reorderLayers(fromIndex: Int, toIndex: Int) {
        editorState.update { oldState ->
            val currentLayers = oldState.layers

            if (fromIndex == toIndex) return@update oldState
            if (fromIndex !in currentLayers.indices || toIndex !in currentLayers.indices) return@update oldState

            if (fromIndex == 0 || toIndex == 0) return@update oldState

            val mutableLayers = currentLayers.toMutableList()
            val movedLayer = mutableLayers.removeAt(fromIndex)
            mutableLayers.add(toIndex, movedLayer)

            var newActiveIndex = oldState.activeLayerIndex

            if (fromIndex == oldState.activeLayerIndex) {
                newActiveIndex = toIndex
            } else if (oldState.activeLayerIndex in (fromIndex + 1)..toIndex) {
                newActiveIndex--
            } else if (oldState.activeLayerIndex in toIndex..<fromIndex) {
                newActiveIndex++
            }

            oldState.copy(
                layers = mutableLayers.toList(),
                activeLayerIndex = newActiveIndex
            )
        }
    }

    private fun processAction(action: CanvasAction, isUndo: Boolean) {
        editorState.update { oldState ->

            val (layerId, targetStroke) = when (action) {
                is CanvasAction.CommitStroke -> action.layerId to action.stroke
            }

            if (oldState.layers.none { it.id == layerId }) return@update oldState

            oldState.copy(
                layers = oldState.layers.map { layer ->
                    if (layer.id == layerId) {
                        layer.copy(
                            strokes = if (isUndo) {
                                layer.strokes - targetStroke
                            } else {
                                layer.strokes + targetStroke
                            }
                        )
                    } else {
                        layer
                    }
                }
            )
        }
    }
}