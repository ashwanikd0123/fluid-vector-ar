package com.example.fluidvectorar.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PointData(
    val x: Float,
    val y: Float,
    val timestamp: Long = System.currentTimeMillis()
)