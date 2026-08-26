package com.example.fluidvectorar.helper

import android.content.Context
import java.io.File

object StorageManager {

    fun getLayerFilePath(context: Context, projectId: String, layerId: String): String {
        // 1. Create a dedicated folder for this project
        val projectDir = File(context.filesDir, "projects/$projectId")

        // Ensure directory exists
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }

        // 2. Generate the filename using the Layer's UUID
        val layerFile = File(projectDir, "layer_$layerId.json")

        // 3. Return the absolute path string to save in Room DB
        return layerFile.absolutePath
    }

    fun deleteProjectFiles(context: Context, projectId: String): Boolean {
        val projectDir = File(context.filesDir, "projects/$projectId")
        return if (projectDir.exists()) {
            projectDir.deleteRecursively() // Deletes folder and all layer JSONs inside it
        } else {
            true
        }
    }

    fun deleteLayer(context: Context, projectId: String, layerId: String): Boolean {
        val path = File(getLayerFilePath(context, projectId, layerId))
        return if (path.exists()) {
            path.delete()
        } else {
            true
        }
    }
}