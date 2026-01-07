package com.ipn.escomoto.domain.repository

import com.ipn.escomoto.domain.model.Motorcycle

interface MotorcycleRepository {
    suspend fun add(moto: Motorcycle): Result<Motorcycle>
    suspend fun update(moto: Motorcycle): Result<Unit>
    suspend fun remove(motoId: String): Result<Unit>
    suspend fun getByOwner(ownerId: String): Result<List<Motorcycle>>
}