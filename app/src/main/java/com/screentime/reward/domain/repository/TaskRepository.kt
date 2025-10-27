package com.screentime.reward.domain.repository

import com.screentime.reward.domain.model.Task
import com.screentime.reward.domain.model.UserRole
import com.screentime.reward.domain.model.TimeInfo
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    
    // Получить все задачи для ребенка
    fun getChildTasks(): Flow<List<Task>>
    
    // Получить задачи, ожидающие утверждения (для взрослого)
    fun getPendingTasks(): Flow<List<Task>>
    
    // Создать новую задачу
    suspend fun createTask(name: String, timeMinutes: Int): Long
    
    // Утвердить задачу
    suspend fun approveTask(taskId: Long)
    
    // Отклонить задачу
    suspend fun rejectTask(taskId: Long)
    
    // Получить информацию о доступном времени
    fun getTimeInfo(): Flow<TimeInfo>
    
    // Получить базовое время
    fun getBaseTimeHours(): Flow<Int>
    
    // Установить базовое время
    suspend fun setBaseTimeHours(hours: Int)
    
    // Получить текущую роль пользователя
    fun getCurrentRole(): Flow<UserRole>
    
    // Установить роль пользователя
    suspend fun setCurrentRole(role: UserRole)
}

