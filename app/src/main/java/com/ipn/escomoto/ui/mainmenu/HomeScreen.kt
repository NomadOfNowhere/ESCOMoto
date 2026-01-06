package com.ipn.escomoto.ui.mainmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.ui.mainmenu.components.MotorcycleCard
import com.ipn.escomoto.ui.mainmenu.components.PendingRequestCard
import com.ipn.escomoto.ui.mainmenu.components.QuickActionCard
import com.ipn.escomoto.ui.mainmenu.components.SystemStatsCard

@Composable
fun HomeScreen(
    name: String,
    escomId: String,
    userType: String,
    modifier: Modifier
) {
    // Animación de entrada para elementos
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item {
            // Header con saludo - Animación de fade in y slide
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(initialOffsetY = { -40 })
            ) {
                Column {
                    Text(
                        text = "¡Hola, ${name.split(" ")[0]}!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (userType == "Visitante") "Tu plan es temporal" else "Boleta: $escomId",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 100))
            ) {
                Text(
                    text = "Acciones rápidas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                        slideInVertically(
                            initialOffsetY = { 50 },
                            animationSpec = tween(600, delayMillis = 200)
                        )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Login,
                        title = "Check-in",
//                        color = Color(0xFF7C4DFF),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                    QuickActionCard(
                        icon = Icons.Default.Logout,
                        title = "Check-out",
                        color = Color(0xFFFF6E40),
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Sección según tipo de usuario
        if (userType == "Supervisor" || userType == "Administrador") {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                ) {
                    Text(
                        text = "Solicitudes pendientes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 400)
                            )
                ) {
                    Column {
                        PendingRequestCard(
                            userName = "María González",
                            userEscomId = "2020630456",
                            motorcyclePlate = "ABC-123",
                            requestType = "Check-in"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
        if (userType == "Administrador") {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                ) {
                    Text(
                        text = "Resumen del sistema",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 400)
                            )
                ) {
                    Column {
                        SystemStatsCard()
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        } else {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                ) {
                    Text(
                        text = "Mis motocicletas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 400)
                            )
                ) {
                    Column {
                        MotorcycleCard(
                            plate = "XYZ-789",
                            model = "Yamaha MT-07",
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}