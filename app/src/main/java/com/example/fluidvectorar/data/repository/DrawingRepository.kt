package com.example.fluidvectorar.data.repository

import com.example.fluidvectorar.data.local.dao.LayerEntityDao
import com.example.fluidvectorar.data.local.dao.ProjectEntityDao
import com.example.fluidvectorar.data.local.entity.LayerEntity
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import javax.inject.Inject

class DrawingRepository @Inject constructor(
    val projectDao: ProjectEntityDao,
    val layerDao: LayerEntityDao
) {
    suspend fun addProject(project: ProjectEntity) = projectDao.addOrUpdateProject(project)

    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)

    suspend fun getProject(id: String) = projectDao.getProject(id)

    fun getAllProjects() = projectDao.getAllProjects()

    suspend fun addOrUpdateLayer(layer: LayerEntity) = layerDao.addOrUpdateLayer(layer)

    suspend fun deleteLayer(layer: LayerEntity) = layerDao.deleteLayer(layer)

    suspend fun getAllLayersInProject(projectId: String) = layerDao.getAllLayersInProject(projectId)
}