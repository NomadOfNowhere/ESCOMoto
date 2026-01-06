package com.ipn.escomoto.ui.mainmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ServicesScreen(userType: String, modifier: Modifier) {
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
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(initialOffsetY = { -40 })
            ) {
                Text(
                    text = "Servicios",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        if (userType == "Supervisor" || userType == "Administrador") {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 100)
                            )
                ) {
                    Column {
                        ServiceMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Solicitudes",
                            description = "Gestionar check-in y check-out",
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
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
                    Column {
                        ServiceMenuItem(
                            icon = Icons.Default.LocalParking,
                            title = "Estacionamiento",
                            description = "Ver disponibilidad",
                            onClick = { }
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
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 100)
                            )
                ) {
                    Column {
                        ServiceMenuItem(
                            icon = Icons.Default.PersonAdd,
                            title = "Supervisores",
                            description = "Gestionar cuentas",
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
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
                    ServiceMenuItem(
                        icon = Icons.Default.Assessment,
                        title = "Reportes",
                        description = "Ver estadísticas",
                        onClick = { }
                    )
                }
            }
        } else {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 100)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 100)
                            )
                ) {
                    Column {
                        ServiceMenuItem(
                            icon = Icons.Default.DirectionsBike,
                            title = "Mis motocicletas",
                            description = "Gestionar vehículos",
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
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
                    ServiceMenuItem(
                        icon = Icons.Default.History,
                        title = "Historial",
                        description = "Ver registros",
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceMenuItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale",
        finishedListener = { pressed = false }
    )

    Card(
        onClick = {
            pressed = true
            onClick()
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF7C4DFF).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF7C4DFF),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}