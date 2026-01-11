package com.ipn.escomoto.di

import com.ipn.escomoto.data.repository.AccessRepositoryImplFirebase
import com.ipn.escomoto.data.repository.AuthRepositoryImplFirebase
import com.ipn.escomoto.data.repository.MotorcycleRepositoryImplFirebase
import com.ipn.escomoto.data.repository.HistoryRepositoryImplFirebase
import com.ipn.escomoto.data.repository.SystemRepositoryImplFirebase
import com.ipn.escomoto.domain.repository.AccessRepository
import com.ipn.escomoto.domain.repository.AuthRepository
import com.ipn.escomoto.domain.repository.MotorcycleRepository
import com.ipn.escomoto.domain.repository.HistoryRepository
import com.ipn.escomoto.domain.repository.SystemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImplFirebase
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMotorcycleRepository(
        impl: MotorcycleRepositoryImplFirebase
    ): MotorcycleRepository

    @Binds
    @Singleton
    abstract fun bindAccessRepository(
        impl: AccessRepositoryImplFirebase
    ): AccessRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        impl: HistoryRepositoryImplFirebase
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSystemRepository(
        impl: SystemRepositoryImplFirebase
    ): SystemRepository
}