package com.ipn.escomoto.domain.model

import java.util.Date

data class SystemSettings(
    val systemEnabled: Boolean = true,
    val checksEnabled: Boolean = true,
    val lastUpdated: Date? = null,
    val updatedBy: String = "Administrador"
)