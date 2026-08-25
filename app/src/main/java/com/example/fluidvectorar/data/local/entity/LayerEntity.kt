package com.example.fluidvectorar.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "layers",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId"])]
)
data class LayerEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val layerIndex: Int,
    val name: String,
    val opacity: Float = 1.0f,
    val blendMode: String = "NORMAL",
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val strokesJsonPath: String // Local file path where JSON/Binary path data is stored
)