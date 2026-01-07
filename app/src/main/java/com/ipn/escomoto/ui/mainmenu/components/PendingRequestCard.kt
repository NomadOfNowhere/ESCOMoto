package com.ipn.escomoto.ui.mainmenu.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PendingRequestCard(userName: String, userEscomId: String, motorcyclePlate: String, requestType: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF7C4DFF),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = userName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text(text = "Boleta: $userEscomId", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Placas: $motorcyclePlate", color = MaterialTheme.colorScheme.onBackground)
            Text(text = "Tipo: $requestType", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                var approvePressed by remember { mutableStateOf(false) }
                var rejectPressed by remember { mutableStateOf(false) }

                val approveScale by animateFloatAsState(
                    targetValue = if (approvePressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "approve_scale",
                    finishedListener = { approvePressed = false }
                )

                val rejectScale by animateFloatAsState(
                    targetValue = if (rejectPressed) 0.95f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "reject_scale",
                    finishedListener = { rejectPressed = false }
                )

                Button(
                    onClick = { approvePressed = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    modifier = Modifier
                        .weight(1f)
                        .scale(approveScale)
                ) {
                    Text("Aprobar", color = Color.Black)
                }
                // CHECAR ESTOS COLORES DESPUÉS
                OutlinedButton(
                    onClick = { rejectPressed = true },
                    modifier = Modifier
                        .weight(1f)
                        .scale(rejectScale)
                ) {
                    Text("Rechazar", color = Color.White)
                }
            }
        }
    }
}