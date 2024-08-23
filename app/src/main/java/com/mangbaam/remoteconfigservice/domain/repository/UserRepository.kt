package com.mangbaam.remoteconfigservice.domain.repository

import com.mangbaam.remoteconfigservice.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User>
}
