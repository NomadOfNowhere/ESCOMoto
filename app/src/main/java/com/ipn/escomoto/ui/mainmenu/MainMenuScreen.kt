package com.ipn.escomoto.ui.mainmenu

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.ui.mainmenu.components.BottomNavigationBar
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.ipn.escomoto.ui.common.SnackbarType
import com.ipn.escomoto.ui.components.StyledSnackbar
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ipn.escomoto.ui.adminview.AdminScreen
import com.ipn.escomoto.ui.history.HistoryScreen

@Composable
fun MainMenuScreen(
    user: User?,
    onLogout: () -> Unit,
    viewModel: MainMenuViewModel = hiltViewModel()
) {
    val selectedTab = viewModel.selectedTab
    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = viewModel.errorMessage
    val isDarkTheme = isSystemInDarkTheme()
    val currentAccessRequest = viewModel.currentAccessRequest
    val pendingRequests by viewModel.pendingRequests.collectAsStateWithLifecycle()

    // Pantalla de estado de los checks
    if (currentAccessRequest != null) {
        AccessStatusScreen(
            accessRequest = currentAccessRequest,
            onDismiss = { viewModel.clearCurrentRequest() },
            onCancel = { viewModel.cancelCurrentRequest() }
        )
    }

    // Manejo de Errores
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                withDismissAction = true,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    LaunchedEffect(user?.id) {
        user?.id?.let { id ->
            if (id.isNotEmpty()) {
                viewModel.loadMotorcycles(id)
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            ) { data ->
                StyledSnackbar(
                    message = data.visuals.message,
                    isDarkTheme = isDarkTheme,
                    onDismiss = { data.dismiss() },
                    type = SnackbarType.ERROR
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) },
                userType = user?.userType ?: "Visitante"
            )
        }
    ) { padding ->
        val contentModifier = Modifier.padding(padding)

        // Animación de transición entre tabs
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { if (targetState > initialState) 300 else -300 }
                        ) togetherWith
                        fadeOut(animationSpec = tween(300)) +
                        slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { if (targetState > initialState) -300 else 300 }
                        )
            },
            label = "tab_animation"
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    // Datos
                    name = user?.name ?: "Usuario",
                    escomId = user?.escomId ?: "",
                    userType = user?.userType ?: "Visitante",
                    email  = user?.email ?: "Usuario",
                    modifier = contentModifier,

                    // Estados del ViewModel
                    motorcycles = viewModel.motorcycles,
                    isLoading = viewModel.isLoading,
                    isUserInside = viewModel.isUserInside,
                    updatingMotorcycleId = viewModel.updatingMotorcycleId,
                    showMotoSelector = viewModel.showMotoSelector,

                    // Eventos CRUD
                    onRefresh = { viewModel.loadMotorcycles(user?.id ?: "") },
                    onAddMotorcycle = { brand, model, plate, imageUri ->
                        viewModel.addMotorcycle(
                            Motorcycle(
                                id = "",
                                ownerId = user?.id ?: "",
                                ownerName = user?.name ?: "",
                                brand = brand,
                                model = model,
                                licensePlate = plate,
                                imageUrl = ""
                            ), imageUri = imageUri
                        )
                    },
                    onUpdateMotorcycle = { moto, brand, model, plate, uri ->
                        viewModel.updateMotorcycle(moto,
                            newBrand = brand,
                            newModel = model,
                            newPlate = plate,
                            newImageUri = uri)
                    },
                    onDeleteMotorcycle = { motoId -> viewModel.deleteMotorcycle(motoId) },

                    // Eventos check-in
                    onCheckInTap = { viewModel.onCheckInTap() },
                    onCheckOutTap = { viewModel.onCheckOutTap() },
                    onMotoSelected = { viewModel.onMotoSelectedForCheckIn(it) },
                    onDismissSelector = { viewModel.dismissMotoSelector() },
                    pendingRequests = pendingRequests,
                    onApproveRequest = { viewModel.approveRequest(it) },
                    onRejectRequest = { viewModel.rejectRequest(it) }
                )
                1 -> HistoryScreen(user?.userType ?: "Visitante", user?.id ?: null, contentModifier)
                2 -> AdminScreen(contentModifier)
                3 -> ServicesScreen(user?.userType ?: "Visitante", contentModifier)
                4 -> ProfileScreen(
                    name = user?.name ?: "Usuario",
                    userType = user?.userType ?: "Visitante",
                    escomId = user?.escomId ?: "",
                    modifier = contentModifier,
                    onLogout = onLogout
                )
            }
        }
    }
}