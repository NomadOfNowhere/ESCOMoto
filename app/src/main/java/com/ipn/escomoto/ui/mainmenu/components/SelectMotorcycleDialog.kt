package com.ipn.escomoto.ui.mainmenu.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.ui.theme.*

@Composable
fun SelectMotorcycleDialog(
    motorcycles: List<Motorcycle>,
    onDismiss: () -> Unit,
    onMotoSelected: (Motorcycle) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val dialogScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dialog_scale"
    )

    val dialogAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "dialog_alpha"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .scale(dialogScale)
                    .graphicsLayer { alpha = dialogAlpha },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) SurfaceDark else SurfaceLight
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Header con gradiente
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        PurplePrimary,
                                        PurpleLight
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Selecciona tu moto",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Elige con cuál moto ingresarás",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Lista de motocicletas
                    if (motorcycles.isEmpty()) {
                        EmptyMotorcycleState()
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f, fill = false),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(motorcycles) { index, moto ->
                                AnimatedMotorcycleItem(
                                    motorcycle = moto,
                                    index = index,
                                    onClick = {
                                        isVisible = false
                                        onMotoSelected(moto)
                                    },
                                    isDarkTheme = isDarkTheme
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón cancelar
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkTheme) TextSecondary else TextSecondaryLight
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedMotorcycleItem(
    motorcycle: Motorcycle,
    index: Int,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    var isVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        isVisible = true
    }

    val offsetX by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 50.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "item_offset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "item_alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "item_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = offsetX)
            .scale(scale)
            .graphicsLayer { this.alpha = alpha }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                Color(0xFF252538)
            } else {
                Color(0xFFF0F2FF)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de la moto con fondo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PurplePrimary.copy(alpha = 0.2f),
                                PurpleLight.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsBike,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información de la moto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = motorcycle.model,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PurplePrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = motorcycle.licensePlate,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = PurplePrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Indicador visual
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GreenAccent)
            )
        }
    }
}

@Composable
fun EmptyMotorcycleState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(
                    if (isSystemInDarkTheme()) {
                        Color(0xFF252538)
                    } else {
                        Color(0xFFF0F2FF)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.DirectionsBike,
                contentDescription = null,
                tint = if (isSystemInDarkTheme()) TextSecondary else TextSecondaryLight,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No hay motocicletas",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Primero registra una motocicleta\npara poder hacer check-in",
            fontSize = 14.sp,
            color = if (isSystemInDarkTheme()) TextSecondary else TextSecondaryLight,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}