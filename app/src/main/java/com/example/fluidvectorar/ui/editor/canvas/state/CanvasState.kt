package com.example.fluidvectorar.ui.editor.canvas.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.example.fluidvectorar.domain.model.BrushStyle
import com.example.fluidvectorar.domain.model.StrokeData

enum class EditorMode {
    DRAW,      // Single finger drawing with reticle offset
    PAN_ZOOM   // Pan, Pinch Zoom, Rotate (Drawing disabled)
}

class CanvasState {
    var activeMode by mutableStateOf(EditorMode.DRAW)

    var scale by mutableFloatStateOf(1f)
    var offset by mutableStateOf(Offset.Zero)
    var rotation by mutableFloatStateOf(0f)

    var currentBrushStyle by mutableStateOf(
        BrushStyle(colorHex = 0xFF000000, strokeWidth = 8f)
    )
    var isReticleEnabled by mutableStateOf(true)

    val completedStrokes = mutableStateListOf<StrokeData>()
    var currentPathPoints = mutableStateListOf<Offset>()

    var currentRawTouchScreen by mutableStateOf<Offset?>(null)
    var currentTargetScreen by mutableStateOf<Offset?>(null)

    val reticleOffset = Offset(0f, -90f)

    var isGridEnabled by mutableStateOf(true)
    var gridSizeDp by mutableFloatStateOf(20f)

    fun resetMatrix() {
        scale = 1f
        offset = Offset.Zero
        rotation = 0f
    }
}