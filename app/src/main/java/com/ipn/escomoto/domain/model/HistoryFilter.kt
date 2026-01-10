package com.ipn.escomoto.domain.model

data class HistoryFilter(
    val userId: String? = null,
    val licensePlate: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)