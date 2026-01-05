package com.ipn.escomoto.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ipn.escomoto.ui.auth.AuthScreen
import com.ipn.escomoto.ui.auth.AuthViewModel
import com.ipn.escomoto.ui.components.SplashScreen
import com.ipn.escomoto.ui.mainmenu.MainMenuScreen

@Composable
fun AppNavigation(authViewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()
    var isCheckingSession by remember { mutableStateOf(true) }

    // Esperar a que el ViewModel termine de cargar el usuario
    LaunchedEffect(authViewModel.isUserLoggedIn, authViewModel.isLoading) {
        if (!authViewModel.isLoading) {
            isCheckingSession = false
        }
    }

    if (isCheckingSession) {
        SplashScreen()
    } else {
        // Si hay sesión activa redirigirse a home, de lo contrario a auth
        NavHost(
            navController = navController,
            startDestination = if (authViewModel.isUserLoggedIn) "home" else "auth"
        ) {
            // PANTALLA 1: AuthScreen
            composable("auth") {
                AuthScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                )
            }

            // PANTALLA 2: HOME
            composable("home") {
                MainMenuScreen(
                    user = authViewModel.currentUser,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("auth") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}