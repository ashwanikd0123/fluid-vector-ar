package com.example.fluidvectorar.ui.editor.state

import com.example.fluidvectorar.data.local.entity.ProjectEntity
import com.example.fluidvectorar.domain.model.BrushStyle
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.domain.model.StrokeData

data class EditorUiState(
    val project: ProjectEntity? = null,

    val isLoadingProject: Boolean = true,

    val layers: List<LayerState> = listOf(),
    val selectedLayerIndex: Int = 0,
) {
    val activeLayer: LayerState?
        get() = layers.getOrNull(selectedLayerIndex)
}