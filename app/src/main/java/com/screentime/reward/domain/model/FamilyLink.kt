package com.screentime.reward.domain.model

data class FamilyLink(
    val familyId: String = "",
    val adultDeviceId: String = "",
    val childDeviceId: String? = null,
    val connectionCode: String = "",  // Код для связки (6 цифр)
    val isActive: Boolean = false
) {
    // Пустой конструктор для Firebase
    constructor() : this("", "", null, "", false)
}

data class PendingApproval(
    val taskId: String = "",
    val taskName: String = "",
    val timeMinutes: Int = 0,
    val timestamp: Long = 0,
    val childDeviceId: String = "",
    val status: String = "pending"
) {
    // Пустой конструктор для Firebase
    constructor() : this("", "", 0, 0, "", "pending")
}

enum class DeviceRole {
    ADULT,
    CHILD,
    UNKNOWN
}

