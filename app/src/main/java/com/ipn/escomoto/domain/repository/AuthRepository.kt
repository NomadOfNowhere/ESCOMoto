package com.ipn.escomoto.domain.repository

import android.net.Uri
import com.ipn.escomoto.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<User>
    suspend fun register(user: User, password: String): Result<User>
    suspend fun getEmailByEscomId(escomId: String): Result<String>
    suspend fun getCurrentUser(): Result<User>
    fun logout()
    suspend fun uploadImage(imageUri: Uri): Result<String>
    suspend fun updateImageUrl(userId: String, newUrl: String): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun promoteToSupervisor(userId: String): Result<Unit>
}