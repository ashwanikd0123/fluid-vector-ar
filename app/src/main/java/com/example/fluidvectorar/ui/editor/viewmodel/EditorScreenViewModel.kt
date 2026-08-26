package com.example.fluidvectorar.ui.editor.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluidvectorar.data.repository.DrawingRepository
import com.example.fluidvectorar.ui.editor.state.EditorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditorScreenViewModel @Inject constructor(val drawingRepo: DrawingRepository) : ViewModel() {

    val editorState = MutableStateFlow(EditorUiState())

    fun loadProject(projectId: String) {
        viewModelScope.launch {

            val project = withContext(Dispatchers.IO) {
                drawingRepo.getProject(projectId)
            }


        }
    }
}