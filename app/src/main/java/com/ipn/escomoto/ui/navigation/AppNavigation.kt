package com.ipn.escomoto.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ipn.escomoto.ui.auth.AuthScreen
import com.ipn.escomoto.ui.auth.AuthViewModel
import com.ipn.escomoto.ui.components.SplashScreen
import com.ipn.escomoto.ui.mainmenu.MainMenuScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipn.escomoto.ui.MainViewModel
import com.ipn.escomoto.ui.mainmenu.MainMenuViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    mainMenuViewModel: MainMenuViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    var isCheckingSession by remember { mutableStateOf(true) }

    // Se dispara cada vez que cambie el estado de usuario
    LaunchedEffect(authViewModel.isUserLoggedIn, authViewModel.isLoading) {
        if (!authViewModel.isLoading) {
            val user = authViewModel.currentUser
            if(user != null) {
                mainMenuViewModel.loadMotorcycles(user.id)
            }
            else {
                isCheckingSession = false
            }
        }
    }

    // Se dispara cada vez que cambie el estado de carga del MainMenu
    LaunchedEffect(mainMenuViewModel.isLoading, authViewModel.isLoading) {
        val user = authViewModel.currentUser
        if (!authViewModel.isLoading && user != null) {
            // Si el usuario y motos terminaron de cargarse
            if (!mainMenuViewModel.isLoading) {
                isCheckingSession = false
            }
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
                    viewModel = mainMenuViewModel,
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