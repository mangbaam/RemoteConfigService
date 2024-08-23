package com.mangbaam.remoteconfigservice.domain.model

data class User(
    val nickname: String = "",
    val age: Int = 0,
    val married: Boolean = false,
    val skills: List<Skill> = emptyList(),
)
