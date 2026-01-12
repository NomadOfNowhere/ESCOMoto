package com.ipn.escomoto.domain.model

data class HistoryFilter(
    val searchKeyword: String? = null,
    val licensePlate: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)