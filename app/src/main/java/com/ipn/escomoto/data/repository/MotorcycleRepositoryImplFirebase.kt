package com.ipn.escomoto.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.repository.MotorcycleRepository
import javax.inject.Singleton
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Singleton
class MotorcycleRepositoryImplFirebase @Inject constructor() : MotorcycleRepository{
    private val db = FirebaseFirestore.getInstance().collection("motorcycles")
    private val storageRef = FirebaseStorage.getInstance().reference

    override suspend fun add(moto: Motorcycle): Result<Motorcycle> {
        return try {
            // Creamos un documento nuevo con ID automático
            val docRef = db.document()
            // Creamos una copia e inyectamos el ID
            val motoCopy = moto.copy(id = docRef.id)

            docRef.set(motoCopy).await()

            Result.success(motoCopy)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
    override suspend fun update(moto: Motorcycle): Result<Unit> {
        return try {
            // Buscamos el documento con su ID y actualizamos datos
            db.document(moto.id).set(moto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateImageUrl(motoId: String, newUrl: String): Result<Unit> {
        return try {
            // Buscamos el documento con su ID y actualizamos datos
            Log.d("DEBUG_MOTO", motoId + " " + newUrl)
            db.document(motoId).update("imageUrl", newUrl).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun remove(motoId: String): Result<Unit> {
        return try {
            val docRef = db.document(motoId)
            val snapshot = docRef.get().await()
            val imageUrl = snapshot.getString("imageUrl")

            if(!imageUrl.isNullOrEmpty()) {
                try {
                    val imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // Buscamos el documento con su ID y eliminamos
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getByOwner(ownerId: String): Result<List<Motorcycle>> {
        return try {
            // Buscamos documentos con ownerID
            val snapshot = db.whereEqualTo("ownerId", ownerId).get().await()
            val motos = snapshot.toObjects(Motorcycle::class.java)
            Result.success(motos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            // Generar ID único
            val fileName = "motos/${UUID.randomUUID()}.jpg"
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
}