package com.ipn.escomoto.domain.model

import java.util.Date

data class AccessLog (
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val motorcycleId: String = "",
    val licensePlate: String = "",
    val model: String = "",
    val type: AccessType = AccessType.ENTRY,
    val timestamp: Date = Date()
)