package com.screentime.reward.domain.model

data class UserSettings(
    val baseTimeHours: Int = 2,  // Базовое время в часах
    val currentRole: UserRole = UserRole.CHILD
)

data class TimeInfo(
    val baseTimeHours: Int,
    val approvedTasksTimeMinutes: Int,  // Время из утвержденных задач
    val totalAvailableMinutes: Int      // Итого доступно минут
) {
    fun getTotalTimeString(): String {
        val hours = totalAvailableMinutes / 60
        val minutes = totalAvailableMinutes % 60
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}ч ${minutes}мин"
            hours > 0 -> "${hours}ч"
            minutes > 0 -> "${minutes}мин"
            else -> "0"
        }
    }
}

