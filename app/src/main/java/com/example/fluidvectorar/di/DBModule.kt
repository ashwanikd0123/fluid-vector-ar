package com.example.fluidvectorar.di

import android.content.Context
import androidx.room.Room
import com.example.fluidvectorar.data.local.AppDatabase.AppDatabase
import com.example.fluidvectorar.data.local.AppDatabase.DB_NAME
import com.example.fluidvectorar.data.local.dao.LayerEntityDao
import com.example.fluidvectorar.data.local.dao.ProjectEntityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DBModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
    }

    @Provides
    @Singleton
    fun providesProjectDao(db: AppDatabase): ProjectEntityDao {
        return db.getProjectDao()
    }

    @Provides
    @Singleton
    fun providesLayerDao(db: AppDatabase): LayerEntityDao {
        return db.getLayersDao()
    }
}