package com.example.fluidvectorar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.fluidvectorar.data.local.entity.LayerEntity

@Dao
interface LayerEntityDao {

    @Upsert
    suspend fun addOrUpdateLayer(layer: LayerEntity): Long

    @Delete
    suspend fun deleteLayer(layer: LayerEntity): Int

    @Query("SELECT * FROM layers WHERE projectId = :id ORDER BY layerIndex")
    fun getAllLayersRelatedToProject(id: String): List<LayerEntity>
}