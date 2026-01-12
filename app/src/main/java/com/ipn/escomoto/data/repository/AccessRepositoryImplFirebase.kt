package com.ipn.escomoto.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.StatusType
import com.ipn.escomoto.domain.repository.AccessRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import java.text.Normalizer

@Singleton
class AccessRepositoryImplFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) : AccessRepository {
    private val accessColl = firestore.collection("access_requests")

    override suspend fun createAccessRequest(request: AccessRequest): Result<String> {
        return try {
            val docRef = accessColl.document() // Genera ID automático antes de guardar
            val requestWithId = request.copy(id = docRef.id)
            docRef.set(requestWithId).await()
            Result.success(docRef.id) // Retornamos el ID
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAccessRequestFlow(requestId: String): Flow<AccessRequest> = callbackFlow {
        val subscription = accessColl.document(requestId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
//                    close(error)
                    Log.e("AccessRepo", "Error escuchando solicitud individual: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val log = snapshot.toObject(AccessRequest::class.java)
                    if (log != null) trySend(log)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getPendingRequests(): Flow<List<AccessRequest>> = callbackFlow {
        val subscription = accessColl.whereEqualTo("status", StatusType.PENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AccessRepo", "Error de permisos o red: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val logs = snapshot?.documents?.mapNotNull {
                    it.toObject(AccessRequest::class.java)
                } ?: emptyList()
                trySend(logs)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateRequestStatus(requestId: String, status: StatusType): Result<Unit> {
        return try {
            val acceptedBy = "Xd"
            accessColl.document(requestId).update(
                "status", status,
                "acceptedBy", acceptedBy,
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLastAccess(userId: String): Result<AccessRequest?> {
        return try {
            // Buscamos el último registro APROBADO de este usuario
            val snapshot = accessColl.whereEqualTo("userId", userId)
                .whereEqualTo("status", StatusType.APPROVED)
                .orderBy("requestTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get().await()

            if (snapshot.isEmpty) {
                Result.success(null)
            } else {
                val log = snapshot.documents[0].toObject(AccessRequest::class.java)
                Result.success(log)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPendingRequestByUserId(userId: String): Result<AccessRequest?> {
        return try {
            val snapshot = accessColl.whereEqualTo("userId", userId)
                .whereEqualTo("status", StatusType.PENDING) // Buscamos solo las pendientes
                .limit(1)
                .get().await()

            if (snapshot.isEmpty) {
                Result.success(null)
            } else {
                val request = snapshot.documents[0].toObject(AccessRequest::class.java)
                Result.success(request)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}