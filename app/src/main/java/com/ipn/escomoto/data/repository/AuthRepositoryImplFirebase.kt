package com.ipn.escomoto.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImplFirebase @Inject constructor() : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("Usuario no encontrado"))

            val document = db.collection("users").document(uid).get().await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Error al leer datos del perfil"))
                }
            } else {
                Result.success(mapToDomain(authResult.user!!))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        name: String,
        escomId: String,
        email: String,
        password: String,
        userType: String
    ): Result<User> {
        return try {
            // Llamada a firebase para crear el usuario
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { firebaseUser ->
                // Datos de usuario adicionales
                val newUser = User(
                    id = firebaseUser.uid,
                    email = email,
                    name = name,
                    escomId = if (userType == "ESCOMunidad") escomId else null,
                    userType = userType
                )
                db.collection("users").document(newUser.id).set(newUser).await()

                Result.success(newUser)
            } ?: Result.failure(Exception("Error al crear usuario"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEmailByEscomId(escomId: String): Result<String> {
        return try {
            val query = db.collection("users")
                .whereEqualTo("escomId", escomId)
                .get()
                .await()

            if (!query.isEmpty) {
                val email = query.documents[0].getString("email") ?: ""
                Result.success(email)
            }
            else {
                Result.failure(Exception("No se encontró ningún usuario con ese ESCOMunidad ID"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        val firebaseUser = auth.currentUser ?: return Result.failure(Exception(""))

        return try {
            val document = db.collection("users").document(firebaseUser.uid).get().await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Error al convertir los datos del usuario"))
                }
            } else {
                // Existe en Auth pero no en Firestore
                Result.success(mapToDomain(firebaseUser))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    // Mapper
    private fun mapToDomain(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: "Usuario de ESCOMoto",
            userType = "Visitante",
            escomId = null
        )
    }
}