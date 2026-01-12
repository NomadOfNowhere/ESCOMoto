package com.ipn.escomoto.domain.model

data class AdminStats(
    val admins: String = "",
    val supervisors: String = "",
    val escomunidad: String = "",
    val visitors: String = "",
    val checkInsToday: String = "",
    val checkOutsToday: String = "",
    val totalUsers: String = "",
    val vehicles: String = "",
)