package com.example.fluidvectorar.ui.editor.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fluidvectorar.data.local.entity.LayerEntity

class EditorState {
    var layers by mutableStateOf<List<LayerEntity>>(emptyList())
    var curLayer by mutableStateOf<LayerEntity?>(null)
    var projectTitle by mutableStateOf("")
}