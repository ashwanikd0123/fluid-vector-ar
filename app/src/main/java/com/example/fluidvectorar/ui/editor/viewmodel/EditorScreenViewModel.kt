package com.example.fluidvectorar.ui.editor.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluidvectorar.data.repository.DrawingRepository
import com.example.fluidvectorar.data.repository.StorageRepository
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.domain.model.StrokeData
import com.example.fluidvectorar.domain.model.toLayerState
import com.example.fluidvectorar.ui.editor.state.EditorUiState
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
    val storageRepo: StorageRepository
) : ViewModel() {

    val editorState = MutableStateFlow(EditorUiState())

    fun loadProject(projectId: String) {
        viewModelScope.launch {

            editorState.update {
                EditorUiState(
                    isLoadingProject = true,
                    layers = emptyList(),
                    activeLayerIndex = 0
                )
            }

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
                    isLoadingProject = false,
                    layers = layers,
                    activeLayerIndex = 0
                )
            }
        }
    }

    fun addStrokeToCurLayer(strokeData: StrokeData) {
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
}