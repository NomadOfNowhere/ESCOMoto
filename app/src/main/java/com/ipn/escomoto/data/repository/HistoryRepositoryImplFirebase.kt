package com.ipn.escomoto.data.repository

import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.HistoryFilter
import com.ipn.escomoto.domain.repository.HistoryRepository
import javax.inject.Singleton
import android.util.Log
import java.util.Date

@Singleton
class HistoryRepositoryImplFirebase @Inject constructor() : HistoryRepository {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getHistory(
        userRole: String,
        currentUserId: String,
        filter: HistoryFilter
    ): Result<List<AccessRequest>> {
        return try {
            // Construimos la Query
            var query: Query = firestore.collection("access_requests")
                .whereIn("status", listOf("APPROVED", "REJECTED"))
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)       // limitar registros obtenidos

            // Filtros de Rol
            if (userRole == "ESCOMunidad" || userRole == "Visitante") {
                query = query.whereEqualTo("userId", currentUserId)
            } else {
                // Filtro de admin por usuario
                if (!filter.userId.isNullOrEmpty()) {
                    query = query.whereEqualTo("userId", filter.userId)
                }
            }

            // Filtro por matrícula
            if (!filter.licensePlate.isNullOrEmpty()) {
                val plateToSearch = filter.licensePlate.trim().uppercase()
                query = query.whereEqualTo("motorcyclePlate", plateToSearch)
            }

            // Filtro por Fecha (Rango de inicio-fin de día)
            if (filter.startDate != null && filter.endDate != null) {
                val dateStart = Date(filter.startDate)
                val dateEnd = Date(filter.endDate)

                Log.d("DB_DEBUG", "Buscando entre: $dateStart  Y  $dateEnd")
                query = query
                    .whereGreaterThanOrEqualTo("timestamp", dateStart)
                    .whereLessThanOrEqualTo("timestamp", dateEnd)
            }
            val snapshot = query.get().await()
            val logs = snapshot.toObjects(AccessRequest::class.java)
            Log.d("DB_DEBUG", "Resultados encontrados ${snapshot.size()}")
            Result.success(logs)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("DB_DEBUG", "Error aplicando filtros", e)
            Result.failure(e)
        }
    }
}