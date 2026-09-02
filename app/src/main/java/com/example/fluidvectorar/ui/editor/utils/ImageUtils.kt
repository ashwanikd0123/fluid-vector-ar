package com.example.fluidvectorar.ui.editor.utils

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

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