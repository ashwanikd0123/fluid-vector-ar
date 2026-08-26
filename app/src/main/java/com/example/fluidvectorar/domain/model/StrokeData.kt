package com.example.fluidvectorar.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class StrokeData(
    val id: String = UUID.randomUUID().toString(),
    val points: List<PointData>,
    val brushStyle: BrushStyle,
    val isSmoothed: Boolean = false
)