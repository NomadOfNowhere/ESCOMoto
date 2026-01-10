package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.Motorcycle
import android.net.Uri

interface MotorcycleRepository {
    suspend fun add(moto: Motorcycle): Result<Motorcycle>
    suspend fun update(moto: Motorcycle, hasNewImage: Boolean): Result<Unit>
    suspend fun remove(motoId: String): Result<Unit>
    suspend fun getByOwner(ownerId: String): Result<List<Motorcycle>>
    suspend fun uploadImage(imageUri: Uri): Result<String>
    suspend fun updateImageUrl(motoId: String, newUrl: String): Result<Unit>
}