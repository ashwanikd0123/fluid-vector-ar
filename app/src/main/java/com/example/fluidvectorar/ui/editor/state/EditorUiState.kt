package com.example.fluidvectorar.ui.editor.state

import androidx.compose.runtime.Immutable
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import com.example.fluidvectorar.domain.model.LayerState

@Immutable
data class EditorUiState(
    val project: ProjectEntity? = null,

    val isLoadingProject: Boolean = true,
    val isSavingProject: Boolean = false,

    val layers: List<LayerState> = listOf(),
    val activeLayerIndex: Int = 0,
) {
    val activeLayer: LayerState?
        get() = layers.getOrNull(activeLayerIndex)
}