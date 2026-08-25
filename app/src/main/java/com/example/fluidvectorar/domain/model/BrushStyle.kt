package com.example.fluidvectorar.domain.model
import kotlinx.serialization.Serializable

@Serializable
enum class BrushType {
    PENCIL,
    MARKER,
    GLOW,
    ERASER
}

@Serializable
data class BrushStyle(
    val colorHex: Long,
    val strokeWidth: Float,
    val opacity: Float = 1.0f,
    val brushType: BrushType = BrushType.PENCIL
)