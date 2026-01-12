package com.ipn.escomoto.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.model.SystemSettings
import com.ipn.escomoto.domain.repository.SystemRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemRepositoryImplFirebase @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : SystemRepository {
    private val configDoc = firestore.collection("config").document("general_settings")

    override fun getSystemSettingsFlow(): Flow<SystemSettings> = callbackFlow {
        val subscription = configDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(SystemSettings())
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val settings = snapshot.toObject(SystemSettings::class.java)
                trySend(settings ?: SystemSettings())
            }
            else {
                trySend(SystemSettings())
            }
        }
        awaitClose { subscription.remove() }
    }

    // Obtener estado del sistema
    override suspend fun getSystemSettings(): Result<SystemSettings> {
        return try {
            val snapshot = configDoc.get().await()
            val settings = snapshot.toObject(SystemSettings::class.java)
            Result.success(settings ?: SystemSettings())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar estado del sistema
    override suspend fun updateSystemStatus(isEnabled: Boolean): Result<Unit> {
        return try {
            val adminId = auth.currentUser?.uid ?: return Result.failure(Exception("No autenticado"))
            val updates = mapOf(
                "systemEnabled" to isEnabled,
                "lastUpdated" to FieldValue.serverTimestamp(),
                "updatedBy" to adminId
            )

            configDoc.set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar estados de los checks
    override suspend fun updateChecksStatus(areEnabled: Boolean): Result<Unit> {
        return try {
            val adminId = auth.currentUser?.uid ?: return Result.failure(Exception("No autenticado"))
            val updates = mapOf(
                "checksEnabled" to areEnabled,
                "lastUpdated" to FieldValue.serverTimestamp(),
                "updatedBy" to adminId
            )

            configDoc.set(updates, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isAdmin(): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("No autenticado"))
            val snapshot = firestore.collection("users").document(userId).get().await()
            val role = snapshot.getString("userType") ?: "Visitante"

            // Aceptamos ADMIN y SUPER_ADMIN si manejas jerarquías
            if (role == "Administrador") {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Acceso denegado: El usuario no tiene permisos de administrador."))
            }

        } catch (e: Exception) {
            // Errores de red o de Firestore
            Result.failure(e)
        }
    }
}