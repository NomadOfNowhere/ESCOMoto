package com.ipn.escomoto.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ipn.escomoto.data.repository.AuthRepositoryImpl
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository: AuthRepository = AuthRepositoryImpl()

    var currentUser by mutableStateOf<User?>(null)
        private set
    var isUserLoggedIn by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        executeAuthAction(
            action = {
                // Llamamos al repositorio para obtener la sesión activa
                repository.getCurrentUser()
            },
            onSuccessCallback = { }
        )
    }

    fun login(id: String, pass: String, onLoginSuccess: () -> Unit) {
        if (id.isBlank() || pass.isBlank()) {
            errorMessage = "Por favor llena todos los campos"
            return
        }

        // Pasamos la navegación como el callback de éxito de la acción
        executeAuthAction(
            action = {
                val email = if(id.contains("@")) id else {
                    repository.getEmailByEscomId(id).getOrThrow()
                }
                repository.login(email, pass)
            },
            onSuccessCallback = {
                onLoginSuccess()
            }
        )
    }

    fun register(
        name: String,
        escomId: String,
        email: String,
        password: String,
        confirmPassword: String,
        userType: String,
        onRegisterSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || (userType == "ESCOMunidad" && escomId.isBlank())) {
            errorMessage = "Por favor, llena todos los campos"
            return
        }

        if(userType == "ESCOMunidad" && escomId.length < 10) {
            errorMessage = "Ingresa un ID de ESCOMunidad válido"
            return
        }

        if(password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        executeAuthAction(
            action = { repository.register(name, escomId, email, password, userType) },
            onSuccessCallback = { onRegisterSuccess() }
        )
    }

//    fun registerGuest(onSuccess: () -> Unit) {
//        executeAuthAction {
//            // Un invitado no tiene email ni pass iniciales,
//            // el repo genera un ID temporal
//            repository.register(
//                name = "Invitado",
//                escomId = "",
//                email = "guest_${System.currentTimeMillis()}@escomoto.com",
//                password = "guest_temp_pass",
//                userType = "Visitante"
//            ).onSuccess { onSuccess() }
//        }
//    }

    fun logout() {
        repository.logout()
        currentUser = null
        isUserLoggedIn = false
    }

    private fun executeAuthAction(action: suspend () -> Result<User>, onSuccessCallback: ((User) -> Unit)? = null) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = action()
                result.onSuccess { user ->
                    currentUser = user
                    isUserLoggedIn = true

                    onSuccessCallback?.invoke(user) // Ejecutamos la navegación AQUÍ
                }.onFailure { e ->
                    errorMessage = e.localizedMessage ?: "Error desconocido"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}