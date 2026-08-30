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

    fun fitToViewport(canvasWidth: Int, canvasHeight: Int) {
        if (viewportSize == IntSize.Zero) return

        val viewportWidth = viewportSize.width.toFloat()
        val viewportHeight = viewportSize.height.toFloat()

        val scaleX = viewportWidth / canvasWidth
        val scaleY = viewportHeight / canvasHeight

        // Use a slightly smaller scale to add some padding
        val newScale = minOf(scaleX, scaleY) * 0.9f
        scale = newScale.coerceIn(minScale, maxScale)

        // Center the canvas page (0,0 to W,H) in the viewport
        // The pivot for transformations is at viewportSize / 2
        // We want (canvasWidth/2, canvasHeight/2) in world coords to map to (viewportWidth/2, viewportHeight/2) in screen coords
        // Since the pivot is already at (viewportWidth/2, viewportHeight/2), 
        // a world point (x, y) maps to screen point:
        // screenX = pivotX + (x - pivotX) * scale + offset.x
        // We want screenX = pivotX when x = canvasWidth / 2
        // pivotX = pivotX + (canvasWidth/2 - pivotX) * scale + offset.x
        // 0 = (canvasWidth/2 - pivotX) * scale + offset.x
        // offset.x = -(canvasWidth/2 - pivotX) * scale

        val pivotX = viewportWidth / 2f
        val pivotY = viewportHeight / 2f

        offset = Offset(
            x = -(canvasWidth / 2f - pivotX) * scale,
            y = -(canvasHeight / 2f - pivotY) * scale
        )
        
        rotation = 0f
    }
}