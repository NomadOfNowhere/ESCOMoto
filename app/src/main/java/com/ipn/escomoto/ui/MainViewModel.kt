package com.ipn.escomoto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ipn.escomoto.domain.repository.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val systemRepository: SystemRepository,
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
            val adminCheck = systemRepository.isAdmin()
            val isUserAdmin = adminCheck.isSuccess

            // Observamos cambios en la configuración
            systemRepository.getSystemSettingsFlow().collect { settings ->
                if (settings.systemEnabled) {
                    _appState.value = AppState.Active
                } else {
                    // Si el sistema está en MANTENIMIENTO
                    if (isUserAdmin) {
                        // Los admins ignoran el mantenimiento
                        _appState.value = AppState.Active
                    } else {
                        // Los usuarios normales ven la pantalla de bloqueo
                        _appState.value = AppState.Maintenance
                    }
                }
            }
        }
    }
}