package com.example.fluidvectorar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val thumbnailPath: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)