package com.screentime.reward.data.database

import androidx.room.TypeConverter
import com.screentime.reward.domain.model.TaskStatus
import com.screentime.reward.domain.model.UserRole

class Converters {
    
    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): TaskStatusDB {
        return when (status) {
            TaskStatus.PENDING -> TaskStatusDB.PENDING
            TaskStatus.APPROVED -> TaskStatusDB.APPROVED
            TaskStatus.REJECTED -> TaskStatusDB.REJECTED
        }
    }
    
    @TypeConverter
    fun toTaskStatus(status: TaskStatusDB): TaskStatus {
        return when (status) {
            TaskStatusDB.PENDING -> TaskStatus.PENDING
            TaskStatusDB.APPROVED -> TaskStatus.APPROVED
            TaskStatusDB.REJECTED -> TaskStatus.REJECTED
        }
    }
    
    @TypeConverter
    fun fromUserRole(role: UserRole): UserRoleDB {
        return when (role) {
            UserRole.CHILD -> UserRoleDB.CHILD
            UserRole.ADULT -> UserRoleDB.ADULT
        }
    }
    
    @TypeConverter
    fun toUserRole(role: UserRoleDB): UserRole {
        return when (role) {
            UserRoleDB.CHILD -> UserRole.CHILD
            UserRoleDB.ADULT -> UserRole.ADULT
        }
    }
}

