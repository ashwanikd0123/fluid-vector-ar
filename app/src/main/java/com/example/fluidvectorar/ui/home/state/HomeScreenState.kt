package com.example.fluidvectorar.ui.home.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenState {
    var isProjectListLoading by mutableStateOf(false)
    var isCreatingNewProject by mutableStateOf(false)
    var isDeletingProject by mutableStateOf(false)
    var showingNewProjectDialog by mutableStateOf(false)

    var projectList by mutableStateOf<List<ProjectEntity>>(emptyList())

    var currentProjectName by mutableStateOf("")
    var moveToProject by mutableStateOf<String?>(null)

    fun reset() {
        isProjectListLoading = false
        moveToProject = null
        showingNewProjectDialog = false
        currentProjectName = ""
    }
}