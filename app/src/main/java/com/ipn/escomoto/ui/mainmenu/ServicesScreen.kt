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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MenuOption(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val requiresRole: List<String> = emptyList()
)

@Composable
fun ServicesScreen(userType: String, modifier: Modifier) {
    val allOptions = remember {
        listOf(
            MenuOption(Icons.Default.FactCheck, "Checks", "Realizar check-in/out"),
            MenuOption(Icons.Default.TwoWheeler, "Mis motocicletas", "Gestionar vehículos"),
            MenuOption(Icons.Default.History, "Historial", "Ver registros"),
            MenuOption(Icons.Default.Person, "Mi cuenta", "Ver detalles de la cuenta"),
            MenuOption(Icons.Default.Notifications, "Solicitudes", "Gestionar check-in/out", listOf("Supervisor", "Administrador")),
            MenuOption(Icons.Default.LocalParking, "Estacionamiento", "Ver disponibilidad", listOf("Supervisor", "Administrador")),
            MenuOption(Icons.Default.PersonAdd, "Supervisores", "Gestionar cuentas", listOf("Administrador")),
            MenuOption(Icons.Default.Assessment, "Reportes", "Ver estadísticas", listOf("Administrador"))
        )
    }

    // Filtramos la lista según el usuario
    val filteredOptions = remember(userType) {
        allOptions.filter { it.requiresRole.isEmpty() || it.requiresRole.contains(userType) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Servicios",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .graphicsLayer {
                        alpha = 1f
                    }
            )
        }

        itemsIndexed(filteredOptions) { index, option ->
            ServicesCard(
                icon = option.icon,
                title = option.title,
                description = option.description,
                index = index,
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ServicesCard(
    icon: ImageVector,
    title: String,
    description: String,
    index: Int,
    onClick: () -> Unit
) {
    // Estado para la animación
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

    // El item ya ocupa su espacio, modificamos su opacidad y desplazamiento visual
    Column(
        modifier = Modifier
            .graphicsLayer {
                // Opacidad de 0 a 1
                alpha = animatedProgress.value
                // Desplazamiento vertical: Entra desde abajo (50px) hacia su posición original (0)
                translationY = 50.dp.toPx() * (1f - animatedProgress.value)
            }
    ) {
        ServiceMenuItem(
            icon = icon,
            title = title,
            description = description,
            onClick = onClick
        )
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
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
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