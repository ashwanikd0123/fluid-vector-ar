package com.example.fluidvectorar.ui.editor.canvas.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.example.fluidvectorar.domain.model.BrushStyle
import com.example.fluidvectorar.domain.model.StrokeData

enum class EditorMode {
    DRAW,
    PAN_ZOOM
}

class CanvasState {
    var activeMode by mutableStateOf(EditorMode.DRAW)

    // Matrix Transformations
    var scale by mutableFloatStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)
    var rotation by mutableFloatStateOf(0f)

    // Viewport & Canvas Size Bounds Management
    var viewportSize by mutableStateOf(IntSize.Zero)

    // Scale Limits
    val minScale = 0.5f
    val maxScale = 10.0f

    // Active Path & Reticle States
    var currentRawTouchScreen by mutableStateOf<Offset?>(null)
    var currentTargetScreen by mutableStateOf<Offset?>(null)
    val completedStrokes = mutableStateListOf<StrokeData>()
    var currentPathPoints = mutableStateListOf<Offset>()
    var currentBrushStyle by mutableStateOf(
        BrushStyle(colorHex = 0xFF000000, strokeWidth = 8f)
    )
    var isReticleEnabled by mutableStateOf(true)
    var isGridEnabled by mutableStateOf(true)
    var gridSizeDp by mutableFloatStateOf(20f)
    val reticleOffset = Offset(0f, -90f)

    fun updateTransformations(zoomFactor: Float, panChange: Offset, rotationChange: Float) {
        // 1. Update & Clamp Scale
        val oldScale = scale
        val newScale = (scale * zoomFactor).coerceIn(minScale, maxScale)
        scale = newScale

        // 2. Rotation
        rotation += rotationChange

        // 3. Scale-Adjusted Pan (Multiplied by scale to keep 1:1 finger track speed)
        val adjustedPan = panChange * (newScale / oldScale)

        if (viewportSize != IntSize.Zero) {
            val maxPanX = viewportSize.width * 0.8f * scale
            val maxPanY = viewportSize.height * 0.8f * scale

            val unclampedOffset = offset + adjustedPan
            offset = Offset(
                x = unclampedOffset.x.coerceIn(-maxPanX, maxPanX),
                y = unclampedOffset.y.coerceIn(-maxPanY, maxPanY)
            )
        } else {
            offset += adjustedPan
        }
    }

    fun resetMatrix() {
        scale = 1f
        offset = Offset.Zero
        rotation = 0f
    }
}