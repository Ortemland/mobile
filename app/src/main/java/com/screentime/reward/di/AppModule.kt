package com.screentime.reward.di

import android.content.Context
import com.screentime.reward.data.database.AppDatabase
import com.screentime.reward.data.database.TaskDao
import com.screentime.reward.data.preferences.AppPreferences
import com.screentime.reward.data.repository.TaskRepositoryImpl
import com.screentime.reward.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }
    
    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        preferences: AppPreferences
    ): TaskRepository {
        return TaskRepositoryImpl(taskDao, preferences)
    }
}
