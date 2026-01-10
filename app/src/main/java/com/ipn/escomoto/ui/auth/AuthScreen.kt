package com.ipn.escomoto.ui.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.ipn.escomoto.ui.auth.components.LogoSection
import com.ipn.escomoto.ui.theme.BackgroundGradientEnd
import com.ipn.escomoto.ui.theme.BackgroundGradientEndLight
import com.ipn.escomoto.ui.theme.BackgroundGradientStart
import com.ipn.escomoto.ui.theme.BackgroundGradientStartLight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipn.escomoto.domain.model.User
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var isLogin by remember { mutableStateOf(true) }
    val isDarkTheme = isSystemInDarkTheme()

    /* Animaciones */
    // Estado para controlar la animación inicial
    var isVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Iniciar animaciones cuando se monta el componente
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Animación del logo
    val logoScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "logo_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDarkTheme) {
                        listOf(BackgroundGradientStart, BackgroundGradientEnd)
                    } else {
                        listOf(BackgroundGradientStartLight, BackgroundGradientEndLight)
                    }
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo con animación
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                LogoSection()
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Formulario animado
            AnimatedContent(
                targetState = isLogin,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(
                                animationSpec = tween(300),
                                initialOffsetX = { if (targetState) -it else it }
                            )).togetherWith(
                        fadeOut(animationSpec = tween(300)) +
                                slideOutHorizontally(
                                    animationSpec = tween(300),
                                    targetOffsetX = { if (targetState) it else -it }
                                )
                    )
                },
                label = "auth_animation"
            ) { targetIsLogin ->
                if (targetIsLogin) {
                    LoginForm(
                        isLoading = viewModel.isLoading,
                        errorMessage = viewModel.errorMessage,
                        onLoginClick = { email, pass ->
                            viewModel.login(email, pass, onLoginSuccess)
                        },
                        onSwitchToRegister = { isLogin = false },
                        onForgotPassword = { email ->
                            viewModel.sendPasswordResetEmail(email) {
                                Toast.makeText(
                                    context,
                                    "Correo enviado. Revisa tu bandeja.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                } else {
                    RegisterForm(
                        isLoading = viewModel.isLoading,
                        errorMessage = viewModel.errorMessage,
                        onRegisterSubmit = { name, escomId, email, pass, confirm, type ->
                            viewModel.register(
                                User(
                                    name = name,
                                    escomId = escomId,
                                    email = email,
                                    userType = type
                                ),
                                password = pass,
                                confirmPassword = confirm,
                                onLoginSuccess
                            )
                        },
                        onSwitchToLogin = { isLogin = true }
                    )
                }
            }
        }
    }
}