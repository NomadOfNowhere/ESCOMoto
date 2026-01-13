package com.ipn.escomoto.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.domain.model.StatusType
import com.ipn.escomoto.ui.common.StatusData
import com.ipn.escomoto.ui.theme.TextHint
import com.ipn.escomoto.ui.theme.TextHintLight

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