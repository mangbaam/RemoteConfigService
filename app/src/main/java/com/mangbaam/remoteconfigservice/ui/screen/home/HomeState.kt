package com.mangbaam.remoteconfigservice.ui.screen.home

import com.mangbaam.remoteconfigservice.domain.model.User

data class HomeState(
    val loading: Boolean = false,
    val error: String? = null,
    val user: User = User(),
)
