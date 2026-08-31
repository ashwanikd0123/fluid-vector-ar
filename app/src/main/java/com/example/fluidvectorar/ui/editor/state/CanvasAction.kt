package com.example.fluidvectorar.ui.editor.state

import com.example.fluidvectorar.domain.model.StrokeData

sealed class CanvasAction {
    data class CommitStroke(val layerId: String, val stroke: StrokeData) : CanvasAction()
}