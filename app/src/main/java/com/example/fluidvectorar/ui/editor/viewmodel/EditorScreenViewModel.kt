package com.example.fluidvectorar.ui.editor.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluidvectorar.data.repository.DrawingRepository
import com.example.fluidvectorar.ui.editor.canvas.state.CanvasState
import com.example.fluidvectorar.ui.editor.state.EditorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class EditorScreenViewModel(val drawingRepo: DrawingRepository) : ViewModel() {

    val canvasState = CanvasState()
    val editorState = EditorState()

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val project = withContext(Dispatchers.IO) {
                drawingRepo.getProject(projectId)
            }

        }
    }
}