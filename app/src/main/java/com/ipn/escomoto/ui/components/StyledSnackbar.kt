package com.ipn.escomoto.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.ui.theme.*
import androidx.compose.material.icons.filled.Close
import com.ipn.escomoto.ui.common.SnackbarType

@Composable
fun StyledSnackbar(
    message: String,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    type: SnackbarType = SnackbarType.ERROR
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (type) {
                SnackbarType.ERROR -> if (isDarkTheme) Color(0xFF3A1F1F) else Color(0xFFFFEBEE)
                SnackbarType.SUCCESS -> if (isDarkTheme) Color(0xFF1F3A1F) else Color(0xFFE8F5E9)
                SnackbarType.WARNING -> if (isDarkTheme) Color(0xFF3A2F1F) else Color(0xFFFFF3E0)
                SnackbarType.INFO -> if (isDarkTheme) Color(0xFF1F2A3A) else Color(0xFFE3F2FD)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ícono según el tipo
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (type) {
                    SnackbarType.ERROR -> Color(0xFFD32F2F)
                    SnackbarType.SUCCESS -> Color(0xFF388E3C)
                    SnackbarType.WARNING -> Color(0xFFF57C00)
                    SnackbarType.INFO -> PurplePrimary
                }.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = when (type) {
                        SnackbarType.ERROR -> Icons.Default.Error
                        SnackbarType.SUCCESS -> Icons.Default.CheckCircle
                        SnackbarType.WARNING -> Icons.Default.Warning
                        SnackbarType.INFO -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = when (type) {
                        SnackbarType.ERROR -> Color(0xFFD32F2F)
                        SnackbarType.SUCCESS -> Color(0xFF388E3C)
                        SnackbarType.WARNING -> Color(0xFFF57C00)
                        SnackbarType.INFO -> PurplePrimary
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp)
                )
            }

            // Mensaje
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color.White else Color.Black,
                modifier = Modifier.weight(1f)
            )

            // Botón de cerrar
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = if (isDarkTheme) TextSecondary else TextSecondaryLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

