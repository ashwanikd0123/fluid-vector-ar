package com.example.fluidvectorar.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import com.example.fluidvectorar.data.repository.DrawingRepository
import com.example.fluidvectorar.data.repository.StorageRepository
import com.example.fluidvectorar.ui.home.state.HomeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val drawingRepo : DrawingRepository,
    val storageRepo: StorageRepository
) : ViewModel() {

    var _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var homeScreenState = HomeScreenState()

    init {
        homeScreenState.isProjectListLoading = true
        viewModelScope.launch {
            drawingRepo.getAllProjects().collect {
                homeScreenState.projectList = it
                homeScreenState.isProjectListLoading = false
            }
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            homeScreenState.isDeletingProject = true

            val affectedRows = withContext(Dispatchers.IO) {
                drawingRepo.deleteProject(project)
            }

            if (affectedRows <= 0) {
                viewModelScope.launch {
                    _uiEvent.send("Unable to delete")
                }
                return@launch
            }

            withContext(Dispatchers.IO) {
                storageRepo.deleteProjectFiles(project.id)
            }

            homeScreenState.isDeletingProject = false
        }
    }

    fun createNewProject(title: String) {
        viewModelScope.launch {
            homeScreenState.isCreatingNewProject = true
            val time = System.currentTimeMillis()
            val project = ProjectEntity(
                title = title,
                canvasWidth = 1080,
                canvasHeight = 1960,
                createdAt = time,
                updatedAt = time
            )

            val affectedRows = withContext(Dispatchers.IO) {
                drawingRepo.addProject(project)
            }

            if (affectedRows <= 0) {
                viewModelScope.launch {
                    _uiEvent.send("Unable to create project")
                }
            }

            homeScreenState.isCreatingNewProject = false
            homeScreenState.moveToProject = project.id
        }
    }

}