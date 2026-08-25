package com.example.fluidvectorar.data.repository

import com.example.fluidvectorar.data.local.dao.ProjectEntityDao
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import javax.inject.Inject

class DrawingRepository @Inject constructor(
    val projectDao: ProjectEntityDao
) {
    suspend fun addProject(project: ProjectEntity) = projectDao.addOrUpdateProject(project)

    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)

    suspend fun getProject(id: String) = projectDao.getProject(id)

    fun getAllProjects() = projectDao.getAllProjects()
}