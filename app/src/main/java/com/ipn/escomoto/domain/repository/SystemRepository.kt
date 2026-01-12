package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.SystemSettings
import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    fun getSystemSettingsFlow(): Flow<SystemSettings>
    suspend fun getSystemSettings(): Result<SystemSettings>
    suspend fun updateSystemStatus(isEnabled: Boolean): Result<Unit>
    suspend fun updateChecksStatus(areEnabled: Boolean): Result<Unit>
    suspend fun isAdmin(): Result<Unit>
}