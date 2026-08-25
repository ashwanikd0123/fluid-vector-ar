package com.example.fluidvectorar.data.local.AppDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fluidvectorar.data.local.dao.LayerEntityDao
import com.example.fluidvectorar.data.local.dao.ProjectEntityDao
import com.example.fluidvectorar.data.local.entity.LayerEntity
import com.example.fluidvectorar.data.local.entity.ProjectEntity

val DB_NAME = "fluid-vector-database"

@Database(entities = [ProjectEntity::class, LayerEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase()  {

    abstract fun getProjectDao() : ProjectEntityDao

    abstract fun getLayersDao() : LayerEntityDao

}