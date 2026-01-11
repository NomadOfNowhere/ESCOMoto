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
    val timestamp: Date = Date(),
//    val acceptedBy: String = ""
)