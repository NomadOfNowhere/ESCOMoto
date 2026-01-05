package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<User>
    suspend fun register(
        name: String,
        escomId: String,
        email: String,
        password: String,
        userType: String
    ): Result<User>
    suspend fun getEmailByEscomId(escomId: String): Result<String>
    suspend fun getCurrentUser(): Result<User>
    fun logout()
}