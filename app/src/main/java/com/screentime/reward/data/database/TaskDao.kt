package com.screentime.reward.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE status = 'APPROVED'")
    fun getApprovedTasks(): Flow<List<TaskEntity>>
    
    @Insert
    suspend fun insertTask(task: TaskEntity): Long
    
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?
}

