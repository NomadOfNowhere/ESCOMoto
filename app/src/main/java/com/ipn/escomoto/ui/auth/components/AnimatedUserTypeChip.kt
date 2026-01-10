package com.ipn.escomoto.ui.auth.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.ui.theme.PurplePrimary
import com.ipn.escomoto.ui.theme.TextSecondary
import com.ipn.escomoto.ui.theme.TextSecondaryLight

@Composable
fun AnimatedUserTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) PurplePrimary else {
            if (isDarkTheme) Color(0xFF252538) else Color(0xFFE8EEFF)
        },
        border = if (!selected) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                color = if (selected) Color.White else {
                    if (isDarkTheme) TextSecondary else TextSecondaryLight
                },
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}