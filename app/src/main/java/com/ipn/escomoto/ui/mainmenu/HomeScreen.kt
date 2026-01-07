package com.ipn.escomoto.ui.mainmenu

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.ui.mainmenu.components.AddMotorcycleDialog
import com.ipn.escomoto.ui.mainmenu.components.LoadingCard
import com.ipn.escomoto.ui.mainmenu.components.LoadingSkeletonCard
import com.ipn.escomoto.ui.mainmenu.components.MotorcycleCard
import com.ipn.escomoto.ui.mainmenu.components.PendingRequestCard
import com.ipn.escomoto.ui.mainmenu.components.QuickActionCard
import com.ipn.escomoto.ui.mainmenu.components.SystemStatsCard

@Composable
fun HomeScreen(
    name: String,
    escomId: String,
    userType: String,
    modifier: Modifier,
    motorcycles: List<Motorcycle>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onAddMotorcycle: (String, String, String, Uri) -> Unit
) {
    // Animación de entrada para elementos
    var visible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddMotorcycleDialog(
            onDismiss = { showDialog = false },
            onConfirm = { brand, model, plate, imageUri ->
                onAddMotorcycle(brand, model, plate, imageUri)
                showDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        visible = true
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
//            contentPadding = PaddingValues(bottom = 80.dp)
    )
    {
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
                        // color = Color(0xFF7C4DFF),
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
            items(
                items = motorcycles,
                key = { it.id.ifEmpty { it.hashCode().toString() } }
            ) { moto ->
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
                            plate = moto.licensePlate,
                            model = moto.model,
                            onClick = { }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            if (isLoading) {
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
                            LoadingSkeletonCard()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
            // Si no hay motocicletas y no están cargando
            if(motorcycles.size < 3 && !isLoading) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                                slideInVertically(
                                    initialOffsetY = { 50 },
                                    animationSpec = tween(600, delayMillis = 400)
                                )
                    ) {
                        EmptyMotorcycleState(
                            onAddMotorcycle = { showDialog = true }
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun EmptyMotorcycleState(
    onAddMotorcycle: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono grande
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .clip(CircleShape)
//                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    Icons.Default.DirectionsBike,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(40.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))

//            Text(
//                text = "No tienes motocicletas",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Agrega tu primera motocicleta para comenzar",
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.outline,
//                textAlign = TextAlign.Center
//            )

//            Spacer(modifier = Modifier.height(24.dp))

            // Botón con animación
            var buttonPressed by remember { mutableStateOf(false) }
            val buttonScale by animateFloatAsState(
                targetValue = if (buttonPressed) 0.95f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "button_scale",
                finishedListener = { buttonPressed = false }
            )

            Button(
                onClick = {
                    buttonPressed = true
                    onAddMotorcycle()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonScale),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Agregar motocicleta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}