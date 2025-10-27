package com.screentime.reward.data.repository

import com.screentime.reward.data.database.TaskDao
import com.screentime.reward.data.database.TaskEntity
import com.screentime.reward.data.database.TaskStatusDB
import com.screentime.reward.data.database.UserRoleDB
import com.screentime.reward.data.database.mapper.toDomain
import com.screentime.reward.data.database.mapper.toEntity
import com.screentime.reward.data.preferences.AppPreferences
import com.screentime.reward.domain.model.Task
import com.screentime.reward.domain.model.TaskStatus
import com.screentime.reward.domain.model.TimeInfo
import com.screentime.reward.domain.model.UserRole
import com.screentime.reward.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val preferences: AppPreferences
) : TaskRepository {
    
    override fun getChildTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getPendingTasks(): Flow<List<Task>> {
        return taskDao.getPendingTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun createTask(name: String, timeMinutes: Int): Long {
        val task = TaskEntity(
            name = name,
            timeMinutes = timeMinutes,
            status = TaskStatusDB.PENDING,
            role = UserRoleDB.CHILD,
            createdAt = System.currentTimeMillis()
        )
        return taskDao.insertTask(task)
    }
    
    override suspend fun approveTask(taskId: Long) {
        val task = taskDao.getTaskById(taskId)
        task?.let {
            val updatedTask = it.copy(status = TaskStatusDB.APPROVED)
            taskDao.updateTask(updatedTask)
        }
    }
    
    override suspend fun rejectTask(taskId: Long) {
        val task = taskDao.getTaskById(taskId)
        task?.let {
            val updatedTask = it.copy(status = TaskStatusDB.REJECTED)
            taskDao.updateTask(updatedTask)
        }
    }
    
    override fun getTimeInfo(): Flow<TimeInfo> {
        return taskDao.getApprovedTasks().map { approvedTasks ->
            val baseTimeHours = preferences.getBaseTimeHours()
            val approvedTasksTimeMinutes = approvedTasks.sumOf { it.timeMinutes }
            val totalAvailableMinutes = (baseTimeHours * 60) + approvedTasksTimeMinutes
            
            TimeInfo(
                baseTimeHours = baseTimeHours,
                approvedTasksTimeMinutes = approvedTasksTimeMinutes,
                totalAvailableMinutes = totalAvailableMinutes
            )
        }
    }
    
    override fun getBaseTimeHours(): Flow<Int> {
        return preferences.getBaseTimeHoursFlow()
    }
    
    override suspend fun setBaseTimeHours(hours: Int) {
        preferences.setBaseTimeHours(hours)
    }
    
    override fun getCurrentRole(): Flow<UserRole> {
        return preferences.getCurrentRoleFlow()
    }
    
    override suspend fun setCurrentRole(role: UserRole) {
        preferences.setCurrentRole(role)
    }
}

