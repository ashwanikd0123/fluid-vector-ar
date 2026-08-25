package com.example.fluidvectorar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val thumbnailPath: String?,
    val createdAt: Long,
    val updatedAt: Long
)