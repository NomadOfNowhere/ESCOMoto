package com.ipn.escomoto.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.HistoryFilter
import com.ipn.escomoto.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    var historyLogs by mutableStateOf<List<AccessRequest>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var currentFilter by mutableStateOf(HistoryFilter())
        private set

    // Función para cambiar filtros
    fun updateFilter(newFilter: HistoryFilter) {
        currentFilter = newFilter
    }

    // Función para cargar datos (se llama al inicio y al cambiar filtros)
    fun loadHistory(currentUserRole: String, currentUserId: String?, currentFilter: HistoryFilter) {
        if(currentUserId == null) return

        viewModelScope.launch {
            isLoading = true

            val result = historyRepository.getHistory(
                userRole = currentUserRole,
                currentUserId = currentUserId,
                filter = currentFilter,
            )

            result.onSuccess { logs ->
                historyLogs = logs
            }
            result.onFailure { error ->
                // Manejar error (ej. mostrar Snackbar)
                println("Error cargando historial: ${error.message}")
            }

            isLoading = false
        }
    }
}