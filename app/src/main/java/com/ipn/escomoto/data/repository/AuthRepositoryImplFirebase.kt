package com.ipn.escomoto.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImplFirebase @Inject constructor() : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val storageRef = FirebaseStorage.getInstance().reference


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
        user: User,
        password: String
    ): Result<User> {
        return try {
            // Llamada a firebase para crear el usuario
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            result.user?.let { firebaseUser ->
                // Datos de usuario adicionales
                val newUser = User(
                    id = firebaseUser.uid,
                    email = user.email,
                    name = user.name,
                    escomId = if (user.userType == "ESCOMunidad") user.escomId else null,
                    userType = user.userType
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

    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            // Generar ID único
            val fileName = "officialIds/${UUID.randomUUID()}.jpg"
            val fileRef = storageRef.child(fileName)

            // Subir el archivo (Uri) a la nube
            fileRef.putFile(imageUri).await()

            // Obtener la URL de descarga
            val downloadUrl = fileRef.downloadUrl.await().toString()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateImageUrl(userId: String, newUrl: String): Result<Unit> {
        return try {
            // Buscamos el documento con su ID y actualizamos datos
            Log.d("USER_DEBUG", userId + " " + newUrl)
            db.document(userId).update("imageUrl", newUrl).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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