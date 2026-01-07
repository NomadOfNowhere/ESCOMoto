package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.AccessLog

interface AccessRepository {
    suspend fun registerAccess(log: AccessLog): Result<Unit>
    suspend fun getLastAccess(userId: String): Result<AccessLog?>
}