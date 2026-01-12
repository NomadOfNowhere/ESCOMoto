package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.AdminStats
import com.ipn.escomoto.domain.model.HistoryFilter

interface ReportRepository {
    suspend fun getAccessHistory(
            userRole: String,
            currentUserId: String,
            filter: HistoryFilter
    ): Result<List<AccessRequest>>
    suspend fun getStats(): Result<AdminStats>
}
