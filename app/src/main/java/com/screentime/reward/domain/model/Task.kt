package com.screentime.reward.domain.model

data class Task(
    val id: Long,
    val name: String,
    val timeMinutes: Int,
    val status: TaskStatus,
    val role: UserRole
)

enum class TaskStatus {
    PENDING,    // Ожидает утверждения
    APPROVED,   // Утверждено
    REJECTED    // Отклонено
}

enum class UserRole {
    CHILD,      // Ребенок
    ADULT       // Взрослый
}

