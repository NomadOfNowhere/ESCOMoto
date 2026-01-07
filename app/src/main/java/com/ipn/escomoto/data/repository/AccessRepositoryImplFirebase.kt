package com.ipn.escomoto.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ipn.escomoto.domain.model.AccessLog
import com.ipn.escomoto.domain.repository.AccessRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessRepositoryImpl @Inject constructor() : AccessRepository {
    private val db = FirebaseFirestore.getInstance().collection("access_logs")

    override suspend fun registerAccess(log: AccessLog): Result<Unit> {
        return try {
            val docRef = db.document() // ID automático
            val logWithId = log.copy(id = docRef.id)
            docRef.set(logWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLastAccess(userId: String): Result<AccessLog?> {
        return try {
            // Buscamos el registro más reciente de este usuario
            val snapshot = db.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get().await()

            if (snapshot.isEmpty) {
                Result.success(null) // Nunca ha entrado
            } else {
                val log = snapshot.documents[0].toObject(AccessLog::class.java)
                Result.success(log)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}