package com.example.fluidvectorar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StrokeData(
    val id: String,
    val points: List<PointData>,
    val brushStyle: BrushStyle,
    val isSmoothed: Boolean = false
)