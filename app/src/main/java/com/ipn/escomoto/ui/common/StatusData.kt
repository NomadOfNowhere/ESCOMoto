package com.ipn.escomoto.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// Data class para el badge
data class StatusData(
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val icon: ImageVector,
    val text: String
)