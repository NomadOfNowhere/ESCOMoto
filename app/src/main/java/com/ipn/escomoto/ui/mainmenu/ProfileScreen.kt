package com.ipn.escomoto.ui.mainmenu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Clase de datos unificada
data class UniversalOption(
    val icon: ImageVector,
    val title: String,
    val description: String? = null,
    val requiresRole: List<String> = emptyList(),
    val isDestructive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun ProfileScreen(
    name: String,
    escomId: String,
    userType: String,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    // onNavigate: (String) -> Unit // Descomenta cuando conectes la navegación
) {
    // Construcción de la lista completa
    val allOptions = remember(userType) {
        // Lista de Opciones de Perfil
        val profileSettings = listOf(
            UniversalOption(Icons.Default.Settings, "Configuración", "Ajustes de la app") { /* onNavigate */ },
            UniversalOption(Icons.Default.Help, "Ayuda y soporte") { /* onNavigate */ },
            UniversalOption(Icons.Default.ExitToApp, "Cerrar sesión", isDestructive = true, onClick = onLogout)
        )

        // Lista de Servicios
        val services = buildList {
            if(userType == "Administrador") {
                add(UniversalOption(Icons.Default.FactCheck, "Checks", "Realizar check-in/out") { /* onNavigate */ })
                add(UniversalOption(Icons.Default.TwoWheeler, "Mis motocicletas", "Gestionar vehículos") { /* onNavigate */ })
                add(UniversalOption(Icons.Default.History, "Historial", "Ver registros") { /* onNavigate */ })
                add(UniversalOption(Icons.Default.Person, "Mi cuenta", "Ver detalles de la cuenta") { /* onNavigate */ })

                // Opciones restringidas
                add(UniversalOption(
                    Icons.Default.Notifications, "Solicitudes", "Gestionar check-in/out",
                    requiresRole = listOf("Supervisor", "Administrador")
                ) { /* onNavigate */ },)

                add(UniversalOption(
                    Icons.Default.LocalParking, "Estacionamiento", "Ver disponibilidad",
                    requiresRole = listOf("Supervisor", "Administrador")
                ) { /* onNavigate */ },)

                add(UniversalOption(
                    Icons.Default.PersonAdd, "Supervisores", "Gestionar cuentas",
                    requiresRole = listOf("Administrador")
                ) { /* onNavigate */ },)

                add(UniversalOption(
                    Icons.Default.Assessment, "Reportes", "Ver estadísticas",
                    requiresRole = listOf("Administrador")
                ) { /* onNavigate */ })
            }
        }
        // Merge de listas
        profileSettings + services
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Ítem 0: Título Principal
        item {
            AnimatedItemWrapper(index = 0) {
                Text(
                    text = "Mi cuenta",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }

        // Ítem 1: Header con foto y nombre
        item {
            AnimatedItemWrapper(index = 1) {
                UserProfileHeader(name, escomId, userType)
            }
        }

        // Ítems 2..N: Lista de opciones
        itemsIndexed(allOptions) { listIndex, option ->
            AnimatedItemWrapper(index = listIndex + 2) {
                MergeMenuItem(option = option)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MergeMenuItem(option: UniversalOption) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale",
        finishedListener = { pressed = false }
    )

    val isDestructive = option.isDestructive

    // Definición de colores
    val cardColor = if (isDestructive) Color(0xFFFF6E40).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
    val iconBgColor = if (isDestructive) Color(0xFFFF6E40).copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    val iconTint = if (isDestructive) Color(0xFFFF6E40) else MaterialTheme.colorScheme.primary
    val titleColor = if (isDestructive) Color(0xFFFF6E40) else MaterialTheme.colorScheme.onBackground

    Card(
        onClick = {
            pressed = true
            option.onClick()
        },
        colors = CardDefaults.cardColors(containerColor = cardColor),
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
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    option.icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor,
                )
                if (option.description != null) {
                    Text(
                        text = option.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
fun UserProfileHeader(name: String, escomId: String, userType: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = when (userType) {
                    "ESCOMunidad" -> "Boleta/Empleado: $escomId"
                    "Supervisor", "Administrador" -> "$userType ID: $escomId"
                    else -> "Visitante"
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun AnimatedItemWrapper(
    index: Int,
    content: @Composable () -> Unit
) {
    val animatedProgress = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = index * 100
            )
        )
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha = animatedProgress.value
                translationY = 50.dp.toPx() * (1f - animatedProgress.value)
            }
    ) {
        content()
    }
}