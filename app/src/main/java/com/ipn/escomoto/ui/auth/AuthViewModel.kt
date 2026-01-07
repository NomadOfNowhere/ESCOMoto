package com.ipn.escomoto.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipn.escomoto.data.repository.AuthRepositoryImplFirebase
import com.ipn.escomoto.domain.model.User
import com.ipn.escomoto.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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
                authRepository.getCurrentUser()
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
                    authRepository.getEmailByEscomId(id).getOrThrow()
                }
                authRepository.login(email, pass)
            },
            onSuccessCallback = {
                onLoginSuccess()
            }
        )
    }

    fun register(
        user: User,
        password: String,
        confirmPassword: String,
        onRegisterSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank()
            || (userType == "ESCOMunidad" && escomId!!.isBlank())
            || (userType == "Visitante" && imageUrl!!.isBlank())) {
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
            action = { authRepository.register(name, escomId, email, password, userType) },
            onSuccessCallback = { onRegisterSuccess() }
        )
    }

    fun logout() {
        authRepository.logout()
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