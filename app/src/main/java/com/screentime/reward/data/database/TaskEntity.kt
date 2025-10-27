package com.screentime.reward.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "tasks")
@TypeConverters(Converters::class)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val timeMinutes: Int,
    val status: TaskStatusDB,
    val role: UserRoleDB,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskStatusDB {
    PENDING,
    APPROVED,
    REJECTED
}

enum class UserRoleDB {
    CHILD,
    ADULT
}

