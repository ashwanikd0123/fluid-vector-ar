package com.example.fluidvectorar.ui.editor.view

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.domain.model.BrushStyle
import com.example.fluidvectorar.domain.model.BrushType
import com.example.fluidvectorar.domain.model.LayerState
import com.example.fluidvectorar.domain.model.PointData
import com.example.fluidvectorar.domain.model.StrokeData
import com.example.fluidvectorar.helper.BezierSmoother
import com.example.fluidvectorar.ui.editor.state.CanvasGestureState
import com.example.fluidvectorar.ui.editor.state.CanvasMode

@Composable
fun FluidCanvas(
    modifier: Modifier = Modifier,
    canvasState: CanvasGestureState,
    layers: List<LayerState> = emptyList(),
    activeMode: CanvasMode = CanvasMode.DRAW,
    isReticleEnabled: Boolean = true,
    isGridEnabled: Boolean = true,
    gridSizeDp: Float = 20f,
    currentBrushStyle: BrushStyle = BrushStyle(colorHex = 0xFF000000, strokeWidth = 8f),
    spitStroke: (StrokeData) -> Unit = {}
) {
    val updatedBrushStyle by rememberUpdatedState(currentBrushStyle)
    val updatedSpitStroke by rememberUpdatedState(spitStroke)
    val updatedIsReticleEnabled by rememberUpdatedState(isReticleEnabled)

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                canvasState.viewportSize = size
            }
            .pointerInput(activeMode) {
                if (activeMode == CanvasMode.PAN_ZOOM) {
                    detectTransformGestures { _, pan, zoom, rotation ->
                        canvasState.updateTransformations(
                            zoomFactor = zoom,
                            panChange = pan,
                            rotationChange = rotation
                        )
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
                                    val adjustedScreenTouch = if (updatedIsReticleEnabled) {
                                        rawScreenTouch + canvasState.reticleOffset
                                    } else {
                                        rawScreenTouch
                                    }
                                    canvasState.currentTargetScreen = adjustedScreenTouch

                                    // Inverse Transform Screen Point to Canvas World Point
                                    val worldPoint = screenToWorldCoordinates(
                                        screenPoint = adjustedScreenTouch,
                                        viewportSize = canvasState.viewportSize, // Pivot size
                                        scale = canvasState.scale,
                                        offset = canvasState.offset,
                                        rotation = canvasState.rotation // Rotation added!
                                    )

                                    canvasState.currentPathPoints.add(worldPoint)
                                    change.consume()
                                } else if (canvasState.currentPathPoints.isNotEmpty()) {

                                    val pointsData = canvasState.currentPathPoints.map {
                                        PointData(x = it.x, y = it.y)
                                    }

                                    // 2. Create the final StrokeData object
                                    val newStroke = StrokeData(
                                        points = pointsData,
                                        brushStyle = updatedBrushStyle,
                                        isSmoothed = true
                                    )

                                    // 3. Spit the stroke back to the ViewModel to save in active layer
                                    updatedSpitStroke(newStroke)

                                    canvasState.currentPathPoints.clear()
                                    canvasState.currentRawTouchScreen = null
                                    canvasState.currentTargetScreen = null
                                }
                            }
                        }
                    }
                }
            }
            .graphicsLayer(
                scaleX = canvasState.scale,
                scaleY = canvasState.scale,
                translationX = canvasState.offset.x,
                translationY = canvasState.offset.y,
                rotationZ = canvasState.rotation,
            )
    ) {
        // backgroung grid
        if (isGridEnabled) {
            drawBackgroundGrid(gridSizePx = gridSizeDp.dp.toPx())
        }

        with(drawContext.canvas) {
            saveLayer(bounds = size.toRect(), paint = Paint())
            clipRect(0f, 0f, size.width, size.height)

            // layers
            layers.forEach { layer ->
                if (layer.isVisible) {
                    layer.strokes.forEach { stroke ->
                        val pathPoints = stroke.points.map { Offset(it.x, it.y) }

                        val isEraser = stroke.brushStyle.brushType == BrushType.ERASER

                        if (pathPoints.isNotEmpty()) {
                            val strokePath = BezierSmoother.createSmoothPath(pathPoints)
                            drawPath(
                                path = strokePath,
                                color = if (isEraser) Color.Black else Color(stroke.brushStyle.colorHex),
                                style = Stroke(
                                    width = stroke.brushStyle.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                ),
                                alpha = layer.opacity,
                                blendMode = if (isEraser) BlendMode.Clear else BlendMode.SrcOver
                            )
                        }
                    }
                }
            }

            // current path
            if (canvasState.currentPathPoints.isNotEmpty()) {
                val activePath = BezierSmoother.createSmoothPath(canvasState.currentPathPoints)
                val isEraser = currentBrushStyle.brushType == BrushType.ERASER

                drawPath(
                    path = activePath,
                    color = if (isEraser) Color.Black else Color(currentBrushStyle.colorHex),
                    style = Stroke(
                        width = updatedBrushStyle.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    blendMode = if (isEraser) BlendMode.Clear else BlendMode.SrcOver
                )

                // 4. Render Virtual Reticle Pointer (Crosshair) Over Screen
                if (isReticleEnabled && canvasState.currentPathPoints.isNotEmpty()) {
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

            restore()
        }
    }
}

private fun screenToWorldCoordinates(
    screenPoint: Offset,
    viewportSize: IntSize,
    scale: Float,
    offset: Offset,
    rotation: Float
): Offset {
    if (viewportSize == IntSize.Zero) return screenPoint

    // Compose ka default pivot (center point) jahan se zoom/rotate hota hai
    val pivotX = viewportSize.width / 2f
    val pivotY = viewportSize.height / 2f

    val matrix = Matrix()

    // Exact Compose wali sequence apply karo:
    // 1. Center se Scale
    matrix.postScale(scale, scale, pivotX, pivotY)
    // 2. Center se Rotate
    matrix.postRotate(rotation, pivotX, pivotY)
    // 3. Offset Translation apply karo
    matrix.postTranslate(offset.x, offset.y)

    // Ab is Matrix ko Invert (Reverse) kar do taaki Screen Point -> World Point ban jaye
    val inverseMatrix = Matrix()
    matrix.invert(inverseMatrix)

    val points = floatArrayOf(screenPoint.x, screenPoint.y)
    inverseMatrix.mapPoints(points)

    return Offset(points[0], points[1])
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
        canvasState = CanvasGestureState()
    )
}