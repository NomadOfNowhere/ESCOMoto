package com.ipn.escomoto.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.repository.MotorcycleRepository
import javax.inject.Singleton
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

@Singleton
class MotorcycleRepositoryImplFirebase @Inject constructor(
    private val firestore: FirebaseFirestore
) : MotorcycleRepository{
    private val db = FirebaseFirestore.getInstance().collection("motorcycles")

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

    override suspend fun remove(motoId: String): Result<Unit> {
        return try {
            // Buscamos el documento con su ID y eliminamos
            db.document(motoId).delete().await()
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
}