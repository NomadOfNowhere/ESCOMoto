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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.StatusType
import com.ipn.escomoto.domain.repository.AccessRepository
import dagger.Provides
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImplFirebase @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) : AuthRepository {

    private val usersColl = firestore.collection("users")
    private val storageRef = storage.reference

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception("Usuario no encontrado"))

            val document = usersColl.document(uid).get().await()

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
//            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun register(
        user: User,
        password: String
    ): Result<User> {
        return try {
            // Llamada a fireauth para crear el usuario
            val result = auth.createUserWithEmailAndPassword(user.email, password).await()
            result.user?.let { firebaseUser ->
                // Datos de usuario adicionales
                val newUser = user.copy(id = firebaseUser.uid)

                // Guardar en firebase
                usersColl.document(newUser.id).set(newUser).await()
                Result.success(newUser)
            } ?: Result.failure(Exception("Error al crear usuario"))
        } catch (e: Exception) {
            Result.failure(e)
//            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getEmailByEscomId(escomId: String): Result<String> {
        return try {
            val query = usersColl
                .whereEqualTo("escomId", escomId)
                .limit(1)
                .get()
                .await()

            if (!query.isEmpty) {
                val email = query.documents[0].getString("email") ?: ""
                Result.success(email)
            }
            else {
                Result.failure(Exception("No existe ninguna cuenta asociada a este número de boleta/empleado."))
            }
        } catch (e: Exception) {
            Result.failure(e)
//            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        val firebaseUser = auth.currentUser ?: return Result.failure(Exception(""))

        return try {
            val document = usersColl.document(firebaseUser.uid).get().await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)
                if (user != null) Result.success(user)
                else Result.failure(Exception("Error al leer datos del usuario"))
            } else {
                // Existe en Auth pero no en Firestore
                Result.success(mapToDomain(firebaseUser))
            }
        } catch (e: Exception) {
            Result.failure(e)
//            Result.failure(Exception(e.toUserFriendlyMessage()))
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
//            Result.failure(Exception("Error al subir imagen: ${e.localizedMessage}"))
        }
    }

    override suspend fun updateImageUrl(userId: String, newUrl: String): Result<Unit> {
        return try {
            // Buscamos el documento con su ID y actualizamos datos
            Log.d("USER_DEBUG", "Actualizando foto para " + userId + " " + newUrl)
            usersColl.document(userId).update("imageUrl", newUrl).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
//            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
//            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

//    override suspend fun promoteToSupervisor(userId: String): Result<Unit> {
//        return try {
//            firestore.collection("users")
//                .document(userId)
//                .update("userType", "Supervisor")
//                .await()
//
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

//    override suspend fun getEmailByEscomId(escomId: String): Result<String> {
//        return try {
//            val query = usersColl
//                .whereEqualTo("escomId", escomId)
//                .limit(1)
//                .get()
//                .await()
//
//            if (!query.isEmpty) {
//                val email = query.documents[0].getString("email") ?: ""
//                Result.success(email)
//            }
//            else {
//                Result.failure(Exception("No existe ninguna cuenta asociada a este número de boleta/empleado."))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
////            Result.failure(Exception(e.toUserFriendlyMessage()))
//        }
//    }

    // Mapper
    private fun mapToDomain(firebaseUser: FirebaseUser): User {
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: "Usuario",
            userType = "Visitante",
            escomId = null
        )
    }

    override suspend fun updateUserType(userId: String, newType: String): Result<Unit>  {
        return try {
            // Buscamos el documento con su ID y actualizamos datos
            usersColl.document(userId).update("userType", newType).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
//            Result.failure(Exception(e.toUserFriendlyMessage()))
        }
    }

    override suspend fun getUserTypeProfiles(type: String): Result<List<User>> {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("userType", type)
                .get()
                .await()

            val usersList = querySnapshot.toObjects(User::class.java)
            Result.success(usersList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}