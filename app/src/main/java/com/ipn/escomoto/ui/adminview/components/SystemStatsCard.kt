package com.ipn.escomoto.ui.adminview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ipn.escomoto.domain.model.AdminStats
import com.ipn.escomoto.ui.adminview.StatCard

@Composable
fun SystemStatsCard(
    stats: AdminStats
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                label = "Admins",
                value = stats.admins,
                icon = Icons.Default.Shield,
                containerColor = Color(0xFFE91E63),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Supervisores",
                value = stats.supervisors,
                icon = Icons.Default.ManageAccounts,
                containerColor = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                label = "ESCOMunidad",
                value = stats.escomunidad,
                icon = Icons.Default.School,
                containerColor = Color(0xFF3F51B5),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Visitantes",
                value = stats.visitors,
                icon = Icons.Default.Badge,
                containerColor = Color(0xFF673AB7),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                label = "Entradas hoy",
                value = stats.checkInsToday,
                icon = Icons.Default.Login, // Entrada
                containerColor = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Salidas hoy",
                value = stats.checkOutsToday,
                icon = Icons.Default.Logout, // Salida
                containerColor = Color(0xFFEF6C00),
                modifier = Modifier.weight(1f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(
                label = "Usuarios",
                value = stats.totalUsers,
                icon = Icons.Default.Groups, // Grupo de gente
                containerColor = Color(0xFF0097A7),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Vehículos",
                value = stats.vehicles,
                icon = Icons.Default.TwoWheeler,
                containerColor = Color(0xFF1976D2),
                modifier = Modifier.weight(1f)
            )
        }
    }
}