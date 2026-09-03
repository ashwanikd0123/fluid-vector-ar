package com.example.fluidvectorar.ui.editor.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun rememberImageBitmapFromPath(path: String?): ImageBitmap? {
    var imageBitmap by remember(path) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(path) {
        if (path.isNullOrEmpty()) {
            imageBitmap = null
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            try {
                val file = File(path)
                if (file.exists()) {
                    // File ko decode karke Android Bitmap banao
                    val androidBitmap = BitmapFactory.decodeFile(file.absolutePath)
                    // Use Compose ImageBitmap me convert karke state me daal do
                    imageBitmap = androidBitmap?.asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                imageBitmap = null
            }
        }
    }

    return imageBitmap
}

suspend fun saveImageAndCalculateCenter(
    context: Context,
    uri: Uri,
    canvasWidth: Float,
    canvasHeight: Float
): Pair<String, Offset>? = withContext(Dispatchers.IO) {
    try {
        // 1. Copy to internal storage
        val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
        val fileName = "imported_img_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        // 2. Read dimensions WITHOUT loading full image in RAM (Prevents OOM)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)

        val imgWidth = options.outWidth.toFloat()
        val imgHeight = options.outHeight.toFloat()

        // 3. Middle Modeling Math (Center offset calculation)
        val centerX = (canvasWidth - imgWidth) / 2f
        val centerY = (canvasHeight - imgHeight) / 2f

        // Agar image screen se badi hai, toh default scale down logic bhi laga sakte ho

        return@withContext Pair(file.absolutePath, Offset(centerX, centerY))

    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}