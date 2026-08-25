package com.example.fluidvectorar.ui.editor.canvas.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.fluidvectorar.domain.model.StrokeData
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

object BitmapExporter {

    fun generateAndSaveThumbnail(
        context: Context,
        projectId: String,
        strokes: List<StrokeData>,
        width: Int = 300,
        height: Int = 300
    ): String? {
        if (strokes.isEmpty()) return null

        return try {
            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)

            // Draw White Background
            canvas.drawColor(Color.Transparent.toArgb())

            // Render Strokes onto Canvas Bitmap
            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
            }

            // Simple scaling logic for thumbnail bounds
            strokes.forEach { stroke ->
                paint.color = stroke.brushStyle.colorHex.toInt()
                paint.strokeWidth = stroke.brushStyle.strokeWidth / 2f

                val points = stroke.points
                for (i in 0 until points.size - 1) {
                    canvas.drawLine(
                        points[i].x, points[i].y,
                        points[i + 1].x, points[i + 1].y,
                        paint
                    )
                }
            }

            // Save to internal storage directory
            val thumbnailDir = File(context.filesDir, "thumbnails").apply { mkdirs() }
            val file = File(thumbnailDir, "thumb_$projectId.png")

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, out)
            }

            bitmap.recycle()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}