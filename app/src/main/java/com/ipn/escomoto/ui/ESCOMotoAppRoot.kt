package com.ipn.escomoto.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ipn.escomoto.ui.common.MaintenanceScreen
import com.ipn.escomoto.ui.navigation.AppNavigation

@Composable
fun ESCOMotoAppRoot(
    viewModel: MainViewModel = hiltViewModel()
) {
    // Observamos el estado global
    val appState by viewModel.appState.collectAsStateWithLifecycle()

    // Crossfade hace que el cambio entre pantallas sea suave y elegante
    Crossfade(targetState = appState, label = "RootState") { state ->
        when (state) {
            MainViewModel.AppState.Loading -> {
                // Pantalla blanca o Spinner mientras conecta con Firebase
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            MainViewModel.AppState.Maintenance -> {
                MaintenanceScreen()
            }
            MainViewModel.AppState.Active -> {
                AppNavigation()
            }
        }
    }
}