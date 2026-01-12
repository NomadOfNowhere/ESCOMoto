package com.ipn.escomoto.data.repository

import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.HistoryFilter
import javax.inject.Singleton
import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.ipn.escomoto.domain.model.AdminStats
import com.ipn.escomoto.domain.repository.ReportRepository
import com.ipn.escomoto.utils.getTodayRange
import java.util.Date


@Singleton
class ReportRepositoryImplFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReportRepository {

    override suspend fun getAccessHistory(
        userRole: String,
        currentUserId: String,
        filter: HistoryFilter
    ): Result<List<AccessRequest>> {
        return try {
            // Construimos la Query
            var query: Query = firestore.collection("access_requests")
                .whereIn("status", listOf("APPROVED", "REJECTED"))
                .orderBy("requestTime", Query.Direction.DESCENDING)
                .limit(10)       // limitar registros obtenidos

            // Filtros de Rol
            if (userRole == "ESCOMunidad" || userRole == "Visitante") {
                query = query.whereEqualTo("userId", currentUserId)
            } else {
                // Filtro de admin por usuario
                if (!filter.searchKeyword.isNullOrEmpty()) {
                    val searchText = filter.searchKeyword.trim().lowercase()
                    query = query.whereArrayContains("searchKeywords", searchText)
                }
            }

            // Filtro por matrícula (exacto)
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
                    .whereGreaterThanOrEqualTo("requestTime", dateStart)
                    .whereLessThanOrEqualTo("requestTime", dateEnd)
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

    override suspend fun getStats(): Result<AdminStats> {
        return try {
            val usersColl = firestore.collection("users")
            val accessColl = firestore.collection("access_requests")
            val motorColl = firestore.collection("motorcycles")

            // Consultas de conteo en paralelo para máxima velocidad
            val adminsTask = usersColl.whereEqualTo("userType", "Administrador").count().get(AggregateSource.SERVER)
            val supervisorsTask = usersColl.whereEqualTo("userType", "Supervisor").count().get(AggregateSource.SERVER)
            val visitorsTask = usersColl.whereEqualTo("userType", "Visitante").count().get(AggregateSource.SERVER)
            val escomunidadTask = usersColl.whereEqualTo("userType", "ESCOMunidad").count().get(AggregateSource.SERVER)
            val motosTask = motorColl.count().get(AggregateSource.SERVER)

            val (start, end) = getTodayRange()

            val checkInsTask = accessColl
                .whereEqualTo("status", "APPROVED")
                .whereEqualTo("type", "ENTRY")
                .whereGreaterThanOrEqualTo("requestTime", start)
                .whereLessThanOrEqualTo("requestTime", end)
                .count()
                .get(AggregateSource.SERVER)

            val checkOutsTask = accessColl
                .whereEqualTo("status", "APPROVED")
                .whereEqualTo("type", "EXIT")
                .whereGreaterThanOrEqualTo("requestTime", start)
                .whereLessThanOrEqualTo("requestTime", end)
                .count()
                .get(AggregateSource.SERVER)

            // Esperamos los resultados
            val admins = adminsTask.await().count.toInt()
            val supervisors = supervisorsTask.await().count.toInt()
            val escomunidad = escomunidadTask.await().count.toInt()
            val visitors = visitorsTask.await().count.toInt()
            val vehicles = motosTask.await().count.toInt()
            val checkInsToday = checkInsTask.await().count
            val checkOutsToday = checkOutsTask.await().count
            val totalUsers = admins + supervisors + visitors + escomunidad

            val stats = AdminStats(
                admins = admins.toString(),
                supervisors = supervisors.toString(),
                visitors = visitors.toString(),
                escomunidad = escomunidad.toString(),
                vehicles = vehicles.toString(),
                checkInsToday = checkInsToday.toString(),
                checkOutsToday = checkOutsToday.toString(),
                totalUsers = totalUsers.toString()
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}