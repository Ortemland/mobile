package com.screentime.reward.data.database.mapper

import com.screentime.reward.data.database.TaskEntity
import com.screentime.reward.data.database.TaskStatusDB
import com.screentime.reward.data.database.UserRoleDB
import com.screentime.reward.domain.model.Task
import com.screentime.reward.domain.model.TaskStatus
import com.screentime.reward.domain.model.UserRole

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        name = name,
        timeMinutes = timeMinutes,
        status = status.toDomain(),
        role = role.toDomain()
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        name = name,
        timeMinutes = timeMinutes,
        status = status.toDB(),
        role = role.toDB()
    )
}

fun TaskStatusDB.toDomain(): TaskStatus {
    return when (this) {
        TaskStatusDB.PENDING -> TaskStatus.PENDING
        TaskStatusDB.APPROVED -> TaskStatus.APPROVED
        TaskStatusDB.REJECTED -> TaskStatus.REJECTED
    }
}

fun TaskStatus.toDB(): TaskStatusDB {
    return when (this) {
        TaskStatus.PENDING -> TaskStatusDB.PENDING
        TaskStatus.APPROVED -> TaskStatusDB.APPROVED
        TaskStatus.REJECTED -> TaskStatusDB.REJECTED
    }
}

fun UserRoleDB.toDomain(): UserRole {
    return when (this) {
        UserRoleDB.CHILD -> UserRole.CHILD
        UserRoleDB.ADULT -> UserRole.ADULT
    }
}

fun UserRole.toDB(): UserRoleDB {
    return when (this) {
        UserRole.CHILD -> UserRoleDB.CHILD
        UserRole.ADULT -> UserRoleDB.ADULT
    }
}

