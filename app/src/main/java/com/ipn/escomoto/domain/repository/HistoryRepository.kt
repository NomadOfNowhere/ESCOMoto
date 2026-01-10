package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.HistoryFilter

interface HistoryRepository {
    suspend fun getHistory(
            userRole: String,
            currentUserId: String,
            filter: HistoryFilter
    ): Result<List<AccessRequest>>
}
