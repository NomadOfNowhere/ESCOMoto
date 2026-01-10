package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.StatusType
import kotlinx.coroutines.flow.Flow

interface AccessRepository {
    suspend fun createAccessRequest(request: AccessRequest): Result<String>
    fun getAccessRequestFlow(requestId: String): Flow<AccessRequest>
    fun getPendingRequests(): Flow<List<AccessRequest>>
    suspend fun updateRequestStatus(requestId: String, status: StatusType): Result<Unit>
    suspend fun getLastAccess(userId: String): Result<AccessRequest?>
    suspend fun getPendingRequestByUserId(userId: String): Result<AccessRequest?>
}