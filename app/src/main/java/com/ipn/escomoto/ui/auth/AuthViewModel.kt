package com.ipn.escomoto.ui.auth

import android.net.Uri
import android.util.Log
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
    var snackbarErrorMessage by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        executeAuthAction(
            action = {
                authRepository.getCurrentUser()
            },
            onSuccessCallback = { user ->
                if (user.userType == "Visitante") {
                    val created = user.createdAt?.time ?: 0L
                    val deadline = created + (24 * 60 * 60 * 1000) // 24 horas en ms
                    val now = System.currentTimeMillis()

                    if (now > deadline) {
                        logout()
                        snackbarErrorMessage = "Tus credenciales de visitante caducaron, crea una nueva cuenta"
                    }
                }
            }
        )
    }

    fun clearError() {
        snackbarErrorMessage = null
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
        imageUri: Uri?,
        onRegisterSuccess: () -> Unit
    ) {
        if (user.name.isBlank() || user.email.isBlank() || password.isBlank()) {
            errorMessage = "Por favor, llena todos los campos"
            return
        }

        if (user.userType == "ESCOMunidad" && user.escomId.isNullOrBlank()) {
            errorMessage = "El número de boleta/empleado es obligatorio"
            return
        }

        if(user.userType == "ESCOMunidad" && (user.escomId?.length ?: 0) < 10) {
            errorMessage = "Ingresa un ID de ESCOMunidad válido (10 dígitos)"
            return
        }

        if (user.userType == "Visitante" && imageUri == null) {
            errorMessage = "Debes subir una foto de tu identificación"
            return
        }

        if(password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        executeAuthAction(
            action = { authRepository.register(user, password) },
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

    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit) {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Ingresa un correo válido"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)

            result.onSuccess {
                isLoading = false
                onSuccess()
            }.onFailure { e ->
                isLoading = false
                errorMessage = e.localizedMessage ?: "Error al enviar el correo"
            }
        }
    }
}