package com.ipn.escomoto.ui.adminview

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.HistoryFilter
import com.ipn.escomoto.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ipn.escomoto.domain.model.AdminStats
import com.ipn.escomoto.domain.model.SystemSettings
import com.ipn.escomoto.domain.repository.AccessRepository
import com.ipn.escomoto.domain.repository.AuthRepository
import com.ipn.escomoto.domain.repository.SystemRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository,
    private val systemRepository: SystemRepository
) : ViewModel() {
    var stats by mutableStateOf(AdminStats())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var systemSettings by mutableStateOf(SystemSettings())
        private set

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            isLoading = true
            // cargar del repo
            val result = reportRepository.getStats()

            result.onSuccess { newStats ->
                stats = newStats
                Log.d("AdminViewModel", "Estadísticas cargadas: $newStats")
                isLoading = false
            }
            result.onFailure { error ->
                // Manejar error (ej. mostrar Snackbar)
//                errorMessage = exception.localizedMessage ?: "Error desconocido al cargar datos"
                Log.e("AdminViewModel", "Error cargando stats", error)
                isLoading = false
            }
        }
    }

    fun promoteUser(escomId: String) {
        viewModelScope.launch {
            isLoading = true // Mostramos spinner

            val result = authRepository.promoteToSupervisor(escomId)

            result.onSuccess {
                // 1. Limpiamos errores previos
//                errorMessage = null
                // 2. Opcional: Recargamos todas las estadísticas para ver el +1 en Supervisores
                loadStats()
                // 3. Podrías lanzar un mensaje de éxito
                Log.d("AdminVM", "Usuario $escomId ahora es Supervisor")
            }.onFailure { e ->
//                errorMessage = "No se pudo ascender al usuario: ${e.localizedMessage}"
            }
            isLoading = false
        }
    }

    // Función para cargar la configuración inicial al abrir la pantalla
    fun observeSystemConfig() {
        viewModelScope.launch {
            systemRepository.getSystemSettingsFlow().collect { settings ->
                systemSettings = settings
            }
        }
    }

    // Lógica para el Switch de Sistema
    fun toggleSystem(isEnabled: Boolean) {
        // Actualización optimista (cambia en la UI de inmediato para fluidez)
        val previousSettings = systemSettings
        systemSettings = systemSettings.copy(systemEnabled = isEnabled)

        viewModelScope.launch {
            val result = systemRepository.updateSystemStatus(isEnabled)
            if (result.isFailure) {
                // Si falla la red, regresamos al estado anterior
                systemSettings = previousSettings
//                errorMessage = "Error al actualizar el sistema"
            }
        }
    }

    // Lógica para el Switch de Check-ins
    fun toggleChecks(isEnabled: Boolean) {
        val previousSettings = systemSettings
        systemSettings = systemSettings.copy(checksEnabled = isEnabled)

        viewModelScope.launch {
            val result = systemRepository.updateChecksStatus(isEnabled)
            if (result.isFailure) {
                systemSettings = previousSettings
//                errorMessage = "Error al actualizar accesos"
            }
        }
    }
}