package com.example.fluidvectorar.ui.editor.canvas.view

import android.graphics.Canvas
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.ui.editor.canvas.helper.BezierSmoother
import com.example.fluidvectorar.ui.editor.canvas.state.CanvasState
import com.example.fluidvectorar.ui.editor.canvas.state.EditorMode

@Composable
fun FluidCanvas(
    canvasState: CanvasState,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = canvasState.scale,
                scaleY = canvasState.scale,
                translationX = canvasState.offset.x,
                translationY = canvasState.offset.y,
                rotationZ = canvasState.rotation
            )
            .pointerInput(canvasState.activeMode) {
                if (canvasState.activeMode == EditorMode.PAN_ZOOM) {
                    detectTransformGestures { _, pan, zoom, rotation ->
                        canvasState.scale = (canvasState.scale * zoom).coerceIn(0.1f, 50f)
                        canvasState.rotation += rotation
                        canvasState.offset += pan
                    }
                } else {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val changes = event.changes

                            if (changes.isNotEmpty()) {
                                val change = changes.first()
                                if (change.pressed) {
                                    val rawScreenTouch = change.position
                                    canvasState.currentRawTouchScreen = rawScreenTouch

                                    // Apply Constant Screen Offset
                                    val adjustedScreenTouch = if (canvasState.isReticleEnabled) {
                                        rawScreenTouch + canvasState.reticleOffset
                                    } else {
                                        rawScreenTouch
                                    }
                                    canvasState.currentTargetScreen = adjustedScreenTouch

                                    // Inverse Transform Screen Point to Canvas World Point
                                    val worldPoint = screenToWorldCoordinates(
                                        screenPoint = adjustedScreenTouch,
                                        scale = canvasState.scale,
                                        offset = canvasState.offset
                                    )

                                    canvasState.currentPathPoints.add(worldPoint)
                                    change.consume()
                                } else if (canvasState.currentPathPoints.isNotEmpty()) {
                                    canvasState.currentPathPoints.clear()
                                    canvasState.currentRawTouchScreen = null
                                    canvasState.currentTargetScreen = null
                                }
                            }
                        }
                    }
                }
            }
    ) {
        // backgroung grid
        if (canvasState.isGridEnabled) {
            drawBackgroundGrid(gridSizePx = canvasState.gridSizeDp.dp.toPx())
        }

        // current path
        if (canvasState.currentPathPoints.isNotEmpty()) {
            val activePath = BezierSmoother.createSmoothPath(canvasState.currentPathPoints)
            drawPath(
                path = activePath,
                color = Color(canvasState.currentBrushStyle.colorHex),
                style = Stroke(
                    width = canvasState.currentBrushStyle.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Render Virtual Reticle Pointer (Crosshair) Over Screen
            if (canvasState.isReticleEnabled && canvasState.currentPathPoints.isNotEmpty()) {
                val currentTarget = canvasState.currentPathPoints.last()
                drawCircle(
                    color = Color.Red,
                    radius = 8f,
                    center = currentTarget
                )
                drawCircle(
                    color = Color.Red,
                    radius = 24f,
                    center = currentTarget,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

private fun screenToWorldCoordinates(
    screenPoint: Offset,
    scale: Float,
    offset: Offset
): Offset {
    return Offset(
        x = (screenPoint.x - offset.x) / scale,
        y = (screenPoint.y - offset.y) / scale
    )
}

private fun DrawScope.drawBackgroundGrid(gridSizePx: Float) {
    val gridColor = Color(0xFFE0E0E0) // Subtle Light Grey
    val strokeWidthPx = 1f

    // Vertical Grid Lines
    var x = 0f
    while (x <= size.width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = strokeWidthPx
        )
        x += gridSizePx
    }

    // Horizontal Grid Lines
    var y = 0f
    while (y <= size.height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = strokeWidthPx
        )
        y += gridSizePx
    }
}

@Preview
@Composable
fun PreviewFluidCanvas() {
    FluidCanvas(
        canvasState = CanvasState()
    )
}