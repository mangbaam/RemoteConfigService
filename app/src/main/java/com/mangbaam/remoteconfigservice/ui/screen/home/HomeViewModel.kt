package com.mangbaam.remoteconfigservice.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangbaam.remoteconfigservice.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    fun loadUserData() {
        userRepository
            .getUser()
            .catch { e ->
                _uiState.update { it.copy(error = e.message) }
            }
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onEach { user -> _uiState.update { it.copy(user = user, error = null) } }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .launchIn(viewModelScope)
    }
}
