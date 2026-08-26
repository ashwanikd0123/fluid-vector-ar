package com.example.fluidvectorar.ui.editor.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fluidvectorar.domain.model.BrushStyle
import com.example.fluidvectorar.domain.model.StrokeData

class CanvasUIConfigState {
    var activeMode by mutableStateOf(CanvasMode.DRAW)

    var completedStrokes by mutableStateOf<List<StrokeData>>(emptyList())

    var currentBrushStyle by mutableStateOf(BrushStyle(colorHex = 0xFF000000, strokeWidth = 8f))
    var isReticleEnabled by mutableStateOf(true)
    var isGridEnabled by mutableStateOf(true)
    var gridSizeDp by mutableFloatStateOf(20f)
}