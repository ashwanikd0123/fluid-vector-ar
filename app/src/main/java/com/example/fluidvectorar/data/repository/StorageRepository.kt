package com.example.fluidvectorar.data.repository

import android.content.Context
import com.example.fluidvectorar.data.serializer.VectorPathSerializer
import com.example.fluidvectorar.domain.model.StrokeData
import com.example.fluidvectorar.helper.StorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class StorageRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val vectorPathSerializer: VectorPathSerializer
) {
    fun getLayerFilePath(projectId: String, layerId: String) = StorageManager.getLayerFilePath(context, projectId, layerId)

    fun deleteProjectFiles(projectId: String) = StorageManager.deleteProjectFiles(context, projectId)

    fun deleteLayer(projectId: String, layerId: String) = StorageManager.deleteLayer(context, projectId, layerId)

    fun saveStrokesToFile(file: File, strokes: List<StrokeData>) = vectorPathSerializer.saveStrokesToFile(file, strokes)

    fun readStrokesFromFile(file: File) = vectorPathSerializer.readStrokesFromFile(file)

    fun readStrokesFromLayer(projectId: String, layerId: String): List<StrokeData> {
        val fileName = getLayerFilePath(projectId, layerId)
        return readStrokesFromFile(File(fileName))
    }

    fun saveStrokesToFile(projectId: String, layerId: String, strokes: List<StrokeData>) {
        val fileName = getLayerFilePath(projectId, layerId)
        saveStrokesToFile(File(fileName), strokes)
    }
}