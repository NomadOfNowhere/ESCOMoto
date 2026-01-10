package com.ipn.escomoto.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ipn.escomoto.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

@Composable
fun JumpRotateIcon(
    activeIcon: ImageVector = Icons.Default.FilterListOff,
    defaultIcon: ImageVector = Icons.Default.FilterList,
    showOptions: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotationAnim = remember { Animatable(0f) }
    val translationAnim = remember { Animatable(0f) }

    // Detectamos el click
    LaunchedEffect(showOptions) {
        // Lanzamos dos corrutinas en paralelo: subir/bajar y girar
        // Animación de Salto (Sube y baja)
        launch {
            // Sube
            translationAnim.animateTo(
                targetValue = -10f, // Altura del salto
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
            // Baja (regresa a 0)
            translationAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            )
        }

        // Animación de Giro (0 a 360°)
        launch {
            // Gira una vuelta completa
            rotationAnim.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing)
            )
            rotationAnim.snapTo(0f)
        }
    }

    // Animación de color
    val iconColor by animateColorAsState(
        targetValue = if (showOptions) PurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 600),
        label = "iconColor"
    )

    Icon(
        imageVector = if (showOptions) activeIcon else defaultIcon,
        contentDescription = "Mostrar/Ocultar Filtros",
        tint = iconColor,
        modifier = modifier
            .size(24.dp)
            .graphicsLayer {
                translationY = translationAnim.value
                rotationY = rotationAnim.value      // Giro horizontal (3D)
                cameraDistance = 12f * density      // Mejora el efecto 3D
            }
    )
}
