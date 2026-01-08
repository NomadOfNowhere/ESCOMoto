package com.ipn.escomoto.ui.mainmenu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.ipn.escomoto.domain.model.Motorcycle

@Composable
fun MotorcycleDetailDialog(
    motorcycle: Motorcycle,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column {
                // 1. IMAGEN DE CABECERA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (motorcycle.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = motorcycle.imageUrl,
                            contentDescription = "Foto de la moto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Placeholder si no hay imagen
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center)
                        )
                    }

                    // Botón Cerrar Flotante
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 2. INFORMACIÓN
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Marca (Texto pequeño arriba)
                    Text(
                        text = motorcycle.brand.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Modelo (Texto grande)
                    Text(
                        text = motorcycle.model,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Placa (Destacada)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Placas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = motorcycle.licensePlate,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTONES DE ACCIÓN
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // --- 2. BOTÓN ELIMINAR (ROJO) ---
                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error, // Rojo de Material
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier.size(18.dp)
                        )
                        // Opcional: Texto "Eliminar" si cabe, o solo ícono para ahorrar espacio
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar", maxLines = 1)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // --- BOTÓN EDITAR (PRIMARIO) ---
                    Button(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B68EE)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar", maxLines = 1)
                    }
                }
            }
        }
    }
}