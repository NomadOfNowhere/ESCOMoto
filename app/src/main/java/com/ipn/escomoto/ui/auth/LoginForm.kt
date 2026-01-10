package com.ipn.escomoto.ui.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.ui.auth.components.AnimatedLoginButton
import com.ipn.escomoto.ui.auth.components.AnimatedPasswordField
import com.ipn.escomoto.ui.auth.components.AnimatedTextField
import com.ipn.escomoto.ui.theme.PurpleLight
import com.ipn.escomoto.ui.theme.PurplePrimary
import com.ipn.escomoto.ui.theme.TextHint
import com.ipn.escomoto.ui.theme.TextHintLight
import com.ipn.escomoto.ui.theme.TextSecondary
import com.ipn.escomoto.ui.theme.TextSecondaryLight
import com.ipn.escomoto.ui.auth.components.ForgotPasswordDialog

@Composable
fun LoginForm(
    isLoading: Boolean,
    errorMessage: String? = null,
    onLoginClick: (String, String) -> Unit,  // Pasamos email y password
    onSwitchToRegister: () -> Unit,
    onForgotPassword: (String) -> Unit
) {
    // Estados locales
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
    val focusManager = LocalFocusManager.current
    var showForgotDialog by remember { mutableStateOf(false) }

    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onConfirm = { emailRecovery ->
                onForgotPassword(emailRecovery)
                showForgotDialog = false
            }
        )
    }

    /* Animaciones */
    // Estado para animación inicial
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Animaciones de entrada
    val cardAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "card_alpha"
    )

    val cardOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 50.dp,
        animationSpec = tween(durationMillis = 500),
        label = "card_offset"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .offset(y = cardOffset),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Iniciar sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Ingresa tus credenciales para continuar",
                fontSize = 14.sp,
                color = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo con animación
            AnimatedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email/ESCOM ID",
                leadingIcon = Icons.Default.Email,

                // Funcionalidad de enter en el teclado
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,  // Muestra teclado para correos
                    imeAction = ImeAction.Next          // Enter = Siguiente campo
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) } // Mueve el foco abajo
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña con animación
            AnimatedPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                passwordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = it },

                // Funcionalidad de enter en el teclado
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,  // Muestra teclado para contraseñas
                    imeAction = ImeAction.Done             // Muestra LISTO en el teclado
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()     // Cierra el teclado
                        onLoginClick(email, password) // Enter = Listo
                    }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Olvidé mi contraseña con animación
            var forgotPasswordScale by remember { mutableStateOf(1f) }
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 14.sp,
                color = PurplePrimary,
                modifier = Modifier
                    .align(Alignment.End)
                    .scale(forgotPasswordScale)
                    .clickable {
                        forgotPasswordScale = 0.95f
                        showForgotDialog = true
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mensaje de Error
            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Botón de inicio de sesión animado
            AnimatedLoginButton(
                onClick = { onLoginClick(email, password) },
                isLoading = isLoading,
                text = "Iniciar sesión"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Divisor
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = if (isDarkTheme) TextHint.copy(alpha = 0.3f) else TextHintLight.copy(alpha = 0.3f)
                )
                Text(
                    text = "  o  ",
                    color = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                    fontSize = 14.sp
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = if (isDarkTheme) TextHint.copy(alpha = 0.3f) else TextHintLight.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Crear cuenta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                    fontSize = 14.sp
                )
                var registerScale by remember { mutableStateOf(1f) }
                Text(
                    text = "Regístrate",
                    color = PurpleLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .scale(registerScale)
                        .clickable {
                            registerScale = 0.95f
                            onSwitchToRegister()
                        }
                )
            }
        }
    }
}