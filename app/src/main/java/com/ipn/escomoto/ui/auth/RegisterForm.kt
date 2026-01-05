package com.ipn.escomoto.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.ui.components.AnimatedLoginButton
import com.ipn.escomoto.ui.components.AnimatedPasswordField
import com.ipn.escomoto.ui.components.AnimatedTextField
import com.ipn.escomoto.ui.components.AnimatedUserTypeChip
import com.ipn.escomoto.ui.theme.PurpleLight
import com.ipn.escomoto.ui.theme.PurplePrimary
import com.ipn.escomoto.ui.theme.TextHint
import com.ipn.escomoto.ui.theme.TextHintLight
import com.ipn.escomoto.ui.theme.TextSecondary
import com.ipn.escomoto.ui.theme.TextSecondaryLight

@Composable
fun RegisterForm(
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterSubmit: (String, String, String, String, String, String) -> Unit,  // Nombre, EscomID, Email, Password, ConfirmPassword, Tipo
    onSwitchToLogin: () -> Unit
) {
    var userType by remember { mutableStateOf("ESCOMunidad") }
    var name by remember { mutableStateOf("") }
    var escomId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
    val focusManager = LocalFocusManager.current

    // Estado para animación inicial
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Animación de entrada
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
            containerColor = if (isDarkTheme) Color(0xFF1E1E2E) else MaterialTheme.colorScheme.surface
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
                text = "Crear cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Únete a la ESCOMunidad",
                fontSize = 14.sp,
                color = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de tipo de usuario
            Text(
                text = "Tipo de usuario",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedUserTypeChip(
                    label = "ESCOMunidad",
                    selected = userType == "ESCOMunidad",
                    onClick = { userType = "ESCOMunidad" },
                    modifier = Modifier.weight(1f)
                )
                AnimatedUserTypeChip(
                    label = "Visitante",
                    selected = userType == "Visitante",
                    onClick = { userType = "Visitante" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campos con animación
            AnimatedTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre completo",
                leadingIcon = Icons.Default.Person,

                // Funcionalidad de enter en el teclado
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,   // Muestra teclado para texto
                    imeAction = ImeAction.Next          // Enter = Siguiente campo
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }  // Mueve el foco hacia abajo
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // escomID con transición animada
            AnimatedVisibility(
                visible = userType == "ESCOMunidad",
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    AnimatedTextField(
                        value = escomId,
                        onValueChange = { newValue ->      // Sólo acepta valores numéricos
                            if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                                escomId = newValue
                            }
                        },
                        label = "Boleta/Empleado",
                        leadingIcon = Icons.Default.Badge,

                        // Funcionalidad de enter en el teclado
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,  // Muestra teclado numérico
                            imeAction = ImeAction.Next          // Enter = Siguiente campo
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) } // Mueve el foco hacia abajo
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Info visitante con animación
            AnimatedVisibility(
                visible = userType == "Visitante",
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Surface(
                        color = PurplePrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = PurpleLight,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Se te asignará un ID temporal válido por 24 horas",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            AnimatedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo electrónico",
                leadingIcon = Icons.Default.Email,

                // Funcionalidad de enter en el teclado
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,  // Muestra teclado para correos
                    imeAction = ImeAction.Next          // Enter = Siguiente campo
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) } // Mueve el foco hacia abajo
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                passwordVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = it },

                // Funcionalidad de enter en el teclado
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,  // Muestra teclado para contraseñas
                    imeAction = ImeAction.Next          // Enter = Siguiente campo
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) } // Mueve el foco hacia abajo
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Mínimo 8 caracteres, 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial",
                fontSize = 11.sp,
                color = if (isDarkTheme) TextHint else TextHintLight,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar contraseña",
                passwordVisible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = it },

                // Funcionalidad de enter en el teclado
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,  // Muestra teclado para contraseñas
                    imeAction = ImeAction.Done             // Enter = Listo
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()     // Cierra el teclado
                        onRegisterSubmit(name, escomId, email, password, confirmPassword, userType)
                    }
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sección para subir foto de identificación oficial
            AnimatedVisibility(
                visible = userType == "Visitante",
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary.copy(alpha = 0.1f))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Subir foto",
                                fontSize = 12.sp,
                                color = PurplePrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Text(
                        text = "Foto de identificación oficial",
                        fontSize = 11.sp,
                        color = if (isSystemInDarkTheme()) TextHint else TextHintLight,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Términos con animación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PurplePrimary,
                        uncheckedColor = if (isDarkTheme) TextHint else TextHintLight
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row {
                        Text(
                            text = "Acepto los ",
                            fontSize = 12.sp,
                            color = if (isDarkTheme) TextSecondary else TextSecondaryLight
                        )
                        Text(
                            text = "términos y condiciones",
                            fontSize = 12.sp,
                            color = PurpleLight,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                    }
                    Text(
                        text = "y la política de privacidad",
                        fontSize = 12.sp,
                        color = PurpleLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            AnimatedLoginButton(
                onClick = { onRegisterSubmit(name, escomId, email, password, confirmPassword, userType) },
                isLoading = isLoading,
                text = "Crear cuenta",
                enabled = acceptTerms,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿Ya tienes cuenta? ",
                    color = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                    fontSize = 14.sp
                )
                var loginScale by remember { mutableStateOf(1f) }
                Text(
                    text = "Inicia sesión",
                    color = PurpleLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .scale(loginScale)
                        .clickable {
                            loginScale = 0.95f
                            onSwitchToLogin()
                        }
                )
            }
        }
    }
}