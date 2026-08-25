package com.example.fluidvectorar.data.serializer

import com.example.fluidvectorar.domain.model.StrokeData
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

class VectorPathSerializer @Inject constructor() {

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
    }

    fun saveStrokesToFile(file: File, strokes: List<StrokeData>) {
        val jsonString = json.encodeToString(strokes)
        file.writeText(jsonString)
    }

    fun readStrokesFromFile(file: File): List<StrokeData> {
        if (!file.exists()) return emptyList()

        return try {
            val jsonString = file.readText()
            json.decodeFromString<List<StrokeData>>(jsonString)
        } catch (_: Exception) {
            emptyList()
        }
    }
}
