package com.ipn.escomoto.ui.history.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.AccessType
import com.ipn.escomoto.domain.model.StatusType
import com.ipn.escomoto.ui.theme.*
import androidx.compose.foundation.BorderStroke
import com.ipn.escomoto.utils.toDateString

@Composable
fun HistoryItemCard(
    request: AccessRequest,
    index: Int = 0
) {
    val isDarkTheme = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animación de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 50L)
        isVisible = true
    }

    val offsetX by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 30.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_offset"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    val isEntry = request.type == AccessType.ENTRY
    val statusColor = when (request.status) {
        StatusType.APPROVED -> if (isEntry) Color(0xFF4CAF50) else Color(0xFFFF9800)
        StatusType.REJECTED -> Color(0xFFEF5350)
        else -> if (isDarkTheme) TextHint else TextHintLight
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = offsetX)
            .scale(scale)
            .graphicsLayer { this.alpha = alpha },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                Color(0xFF252538)
            } else {
                Color(0xFFF5F7FF)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 3.dp
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Contenedor de imagen/ícono mejorado
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    if (request.motorcycleImgUrl != null) {
                        // Imagen de la motocicleta
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(request.motorcycleImgUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto de motocicleta",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Overlay con gradiente sutil
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                    } else {
                        // Fondo con gradiente si no hay imagen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            statusColor.copy(alpha = 0.25f),
                                            statusColor.copy(alpha = 0.15f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBike,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Ícono de tipo de acceso (pequeño, en esquina)
                    Surface(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp),
                        shape = CircleShape,
                        color = statusColor,
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isEntry) Icons.Default.Login else Icons.Default.Logout,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Información principal mejorada
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Título con badge de estado integrado
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isEntry) "Entrada" else "Salida",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        MiniStatusIndicator(status = request.status, statusColor = statusColor)
                    }

                    // Fecha y hora
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = request.requestTime.toDateString(),
                            color = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Placa de motocicleta mejorada
                    if (request.motorcyclePlate.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PurplePrimary.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.DirectionsBike,
                                    contentDescription = null,
                                    tint = PurplePrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = request.motorcyclePlate,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurplePrimary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge de estado detallado
                EnhancedStatusBadge(status = request.status, isDarkTheme = isDarkTheme)
            }

            // Barra de progreso inferior con mejor diseño
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                statusColor.copy(alpha = 0.6f),
                                statusColor.copy(alpha = 0.3f),
                                statusColor.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun MiniStatusIndicator(status: StatusType, statusColor: Color) {
    val (color, icon) = when (status) {
        StatusType.APPROVED -> statusColor to Icons.Default.CheckCircle
        StatusType.REJECTED -> Color(0xFFEF5350) to Icons.Default.Cancel
        else -> Color.Gray to Icons.Default.Schedule
    }

    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
fun EnhancedStatusBadge(status: StatusType, isDarkTheme: Boolean) {
    val (backgroundColor, textColor, borderColor, icon, text) = when (status) {
        StatusType.APPROVED -> {
            val green = Color(0xFF4CAF50)
            StatusData(
                green.copy(alpha = 0.15f),
                green,
                green.copy(alpha = 0.3f),
                Icons.Default.CheckCircle,
                "Aprobado"
            )
        }
        StatusType.REJECTED -> {
            val red = Color(0xFFEF5350)
            StatusData(
                red.copy(alpha = 0.15f),
                red,
                red.copy(alpha = 0.3f),
                Icons.Default.Cancel,
                "Rechazado"
            )
        }
        else -> {
            val gray = if (isDarkTheme) TextHint else TextHintLight
            StatusData(
                gray.copy(alpha = 0.15f),
                gray,
                gray.copy(alpha = 0.3f),
                Icons.Default.Schedule,
                "Pendiente"
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Data class para el badge
data class StatusData(
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val text: String
)
