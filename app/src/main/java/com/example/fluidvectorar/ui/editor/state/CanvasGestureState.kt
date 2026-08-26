package com.example.fluidvectorar.ui.editor.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

class CanvasGestureState {
    // 1. Matrix Transformations (Changes at 60 FPS during pinch/pan)
    var scale by mutableFloatStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)
    var rotation by mutableFloatStateOf(0f)

    // Viewport Bounds
    var viewportSize by mutableStateOf(IntSize.Zero)
    val minScale = 0.5f
    val maxScale = 10.0f

    // 2. Active Drawing State (Only exists while finger is on screen)
    var currentRawTouchScreen by mutableStateOf<Offset?>(null)
    var currentTargetScreen by mutableStateOf<Offset?>(null)
    var currentPathPoints = mutableStateListOf<Offset>()

    val reticleOffset = Offset(0f, -90f)

    fun updateTransformations(zoomFactor: Float, panChange: Offset, rotationChange: Float) {
        val oldScale = scale
        val newScale = (scale * zoomFactor).coerceIn(minScale, maxScale)
        scale = newScale

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