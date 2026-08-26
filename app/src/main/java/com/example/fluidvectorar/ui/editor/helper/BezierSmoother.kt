package com.example.fluidvectorar.ui.editor.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

object BezierSmoother {

    fun createSmoothPath(points: List<Offset>): Path {
        val path = Path()
        if (points.isEmpty()) return path

        path.moveTo(points.first().x, points.first().y)

        if (points.size < 3) {
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }
            return path
        }

        for (i in 1 until points.size - 1) {
            val p0 = points[i]
            val p1 = points[i + 1]

            // Mid-point calculation for smooth Bezier transition
            val midX = (p0.x + p1.x) / 2f
            val midY = (p0.y + p1.y) / 2f

            path.quadraticTo(p0.x, p0.y, midX, midY)
        }

        // Draw last segment
        val lastPoint = points.last()
        path.lineTo(lastPoint.x, lastPoint.y)

        return path
    }
}