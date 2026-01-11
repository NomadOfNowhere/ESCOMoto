package com.ipn.escomoto.ui.mainmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ipn.escomoto.domain.model.AccessRequest
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.OutlinedButton
import com.ipn.escomoto.domain.model.AccessType
import com.ipn.escomoto.domain.model.StatusType
import com.ipn.escomoto.ui.common.SystemFeedbackEffect

@Composable
fun AccessStatusScreen(
    accessRequest: AccessRequest,
    onDismiss: () -> Unit,
    onCancel: () -> Unit
) {
    val isEntry = accessRequest.type == AccessType.ENTRY
    val pendingTitle = if (isEntry) "Solicitando Entrada" else "Solicitando Salida"
    val approvedTitle = if (isEntry) "¡Bienvenido!" else "¡Buen viaje!"
    val approvedSubtitle = if (isEntry) "Puedes ingresar al estacionamiento." else "Salida autorizada correctamente."

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // ANIMACIÓN DE CONTENIDO SEGÚN ESTADO
                AnimatedContent(targetState = accessRequest.status, label = "status") { status ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        when (status) {
                            StatusType.PENDING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(64.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 6.dp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(pendingTitle, style = MaterialTheme.typography.headlineMedium)
                                Text("Esperando aprobación del supervisor...", color = Color.Gray)
                            }

                            StatusType.APPROVED -> {
                                SystemFeedbackEffect(true)
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(approvedTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                Text(approvedSubtitle, style = MaterialTheme.typography.titleMedium)
                            }

                            StatusType.REJECTED -> {
                                SystemFeedbackEffect(false)
                                Icon(
                                    Icons.Default.Cancel,
                                    null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(80.dp)
                                )
                                Text("Solicitud Rechazada", style = MaterialTheme.typography.headlineMedium)
                                Text("Contacta al supervisor.", color = Color.Gray)
                            }

                            StatusType.CANCELLED -> {
                                Icon(
                                    Icons.Default.Cancel, // O un ícono de advertencia
                                    null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(80.dp)
                                )
                                Text("Solicitud Cancelada", style = MaterialTheme.typography.headlineMedium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Botón de cerrar (Solo habilitado si NO está pendiente, o para cancelar)
                if (accessRequest.status != StatusType.PENDING) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finalizar")
                    }
                } else {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancelar Solicitud")
                    }
                }
            }
        }
    }
}