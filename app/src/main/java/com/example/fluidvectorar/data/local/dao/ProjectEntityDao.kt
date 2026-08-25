package com.example.fluidvectorar.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectEntityDao {
    @Upsert
    suspend fun addOrUpdateProject(projectEntity: ProjectEntity) : Long

    @Delete
    suspend fun deleteProject(projectEntity: ProjectEntity) : Int

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProject(id: String) : ProjectEntity

    @Query("SELECT * FROM projects")
    fun getAllProjects() : Flow<List<ProjectEntity>>
}