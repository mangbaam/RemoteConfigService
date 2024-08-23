package com.mangbaam.remoteconfigservice.data.repository

import com.mangbaam.remoteconfigservice.data.datasource.UserDataSource
import com.mangbaam.remoteconfigservice.domain.model.User
import com.mangbaam.remoteconfigservice.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
) : UserRepository {
    override fun getUser(): Flow<User> = flow {
        combine(
            flowOf(userDataSource.getNickname()),
            flowOf(userDataSource.getAge()),
            flowOf(userDataSource.getIsMarried()),
            flowOf(userDataSource.getSkills()),
        ) { nickname, age, isMarried, skills ->
            listOf(0, 1).random().let {
                if (it == 1) error("invalid nickname")
            }
            User(nickname, age, isMarried, skills)
        }
            .first()
            .let { emit(it) }
    }
}
