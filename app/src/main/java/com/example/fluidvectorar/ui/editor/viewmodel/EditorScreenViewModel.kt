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
                    selectedLayerIndex = 0
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
                    selectedLayerIndex = 0
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
}