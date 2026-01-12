package com.ipn.escomoto.domain.model

import java.util.Date

data class AccessRequest (
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val motorcycleId: String = "",
    val motorcyclePlate: String = "",
    val motorcycleImgUrl: String? = null,
    val type: AccessType = AccessType.ENTRY,
    val status: StatusType = StatusType.PENDING,
    val requestTime: Date = Date(),
    val searchKeywords: List<String> = emptyList(),
    val acceptedBy: String? = null,
    val processTime: Date? = null,
)