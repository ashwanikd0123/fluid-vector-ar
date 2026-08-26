package com.example.fluidvectorar.domain.model

import com.example.fluidvectorar.data.local.entity.LayerEntity
import kotlinx.serialization.Serializable

@Serializable
data class LayerState(
    val id: String,
    val name: String,
    val opacity: Float = 1.0f,
    val blendMode: String = "NORMAL",
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val strokes: List<StrokeData> = emptyList()
)

fun LayerEntity.toLayerState(parsedStrokes: List<StrokeData>): LayerState {
    return LayerState(
        id = this.id,
        name = this.name,
        opacity = this.opacity,
        blendMode = this.blendMode,
        isVisible = this.isVisible,
        isLocked = this.isLocked,
        strokes = parsedStrokes
    )
}

fun LayerState.toLayerEntity(
    projectId: String,
    layerIndex: Int,
    strokesJsonPath: String
): LayerEntity {
    return LayerEntity(
        id = this.id,
        projectId = projectId,
        layerIndex = layerIndex,
        name = this.name,
        opacity = this.opacity,
        blendMode = this.blendMode,
        isVisible = this.isVisible,
        isLocked = this.isLocked,
        strokesJsonPath = strokesJsonPath
    )
}