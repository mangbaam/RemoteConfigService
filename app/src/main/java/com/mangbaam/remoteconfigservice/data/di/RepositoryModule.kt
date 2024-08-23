package com.mangbaam.remoteconfigservice.data.di

import com.mangbaam.remoteconfigservice.data.repository.UserRepositoryImpl
import com.mangbaam.remoteconfigservice.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    internal abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository
}
