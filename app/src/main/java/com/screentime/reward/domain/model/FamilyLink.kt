package com.screentime.reward.domain.model

data class FamilyLink(
    val familyId: String,
    val adultDeviceId: String,
    val childDeviceId: String?,
    val connectionCode: String,  // Код для связки (6 цифр)
    val isActive: Boolean = false
)

data class PendingApproval(
    val taskId: String,
    val taskName: String,
    val timeMinutes: Int,
    val timestamp: Long,
    val childDeviceId: String
)

enum class DeviceRole {
    ADULT,
    CHILD,
    UNKNOWN
}

