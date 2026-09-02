package com.example.fluidvectorar.domain.model

import androidx.compose.ui.geometry.Offset
import com.example.fluidvectorar.data.local.entity.LayerEntity
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@Serializable
object OffsetSerializer : KSerializer<Offset> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Offset") {
        element<Float>("x")
        element<Float>("y")
    }

    override fun serialize(encoder: Encoder, value: Offset) {
        encoder.encodeStructure(descriptor) {
            encodeFloatElement(descriptor, 0, value.x)
            encodeFloatElement(descriptor, 1, value.y)
        }
    }

    override fun deserialize(decoder: Decoder): Offset {
        return decoder.decodeStructure(descriptor) {
            var x = 0f
            var y = 0f
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> x = decodeFloatElement(descriptor, 0)
                    1 -> y = decodeFloatElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unknown index $index")
                }
            }
            Offset(x, y)
        }
    }
}

@Serializable
data class LayerState(
    val id: String,
    val name: String,
    val opacity: Float = 1.0f,
    val blendMode: String = "NORMAL",
    val isVisible: Boolean = true,
    val isLocked: Boolean = false,
    val strokes: List<StrokeData> = emptyList(),
    val imagePath: String? = null,
    @Serializable(with = OffsetSerializer::class)
    val imageOffset: Offset = Offset.Zero,
    val imageScale: Float = 1f,
    val imageRotation: Float = 0f
)

fun LayerEntity.toLayerState(parsedStrokes: List<StrokeData>): LayerState {
    return LayerState(
        id = this.id,
        name = this.name,
        opacity = this.opacity,
        blendMode = this.blendMode,
        isVisible = this.isVisible,
        isLocked = this.isLocked,
        strokes = parsedStrokes,
        imagePath = this.imagePath,
        imageOffset = Offset(this.imageOffsetX, this.imageOffsetY),
        imageScale = this.imageScale,
        imageRotation = this.imageRotation
    )
}

fun LayerState.toLayerEntity(
    projectId: String,
    layerIndex: Int,
    strokesJsonPath: String
): LayerEntity {
    return LayerEntity(
        id = this.id,
        projectId = projectId,
        layerIndex = layerIndex,
        name = this.name,
        opacity = this.opacity,
        blendMode = this.blendMode,
        isVisible = this.isVisible,
        isLocked = this.isLocked,
        strokesJsonPath = strokesJsonPath,
        imagePath = this.imagePath,
        imageOffsetX = this.imageOffset.x,
        imageOffsetY = this.imageOffset.y,
        imageScale = this.imageScale,
        imageRotation = this.imageRotation
    )
}