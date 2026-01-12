package com.ipn.escomoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipn.escomoto.domain.repository.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val systemRepository: SystemRepository
) : ViewModel() {

    sealed class AppState {
        object Loading : AppState()      // Cargando configuración inicial
        object Active : AppState()       // Sistema ON: App normal
        object Maintenance : AppState()  // Sistema OFF: Pantalla de bloqueo
    }

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState = _appState.asStateFlow()

    init {
        observeSystemStatus()
    }

    private fun observeSystemStatus() {
        viewModelScope.launch {
            systemRepository.getSystemSettingsFlow().collect { settings ->
                if (settings.systemEnabled) {
                    _appState.value = AppState.Active
                }
                else {
                    _appState.value = AppState.Maintenance
                }
            }
        }
    }
}