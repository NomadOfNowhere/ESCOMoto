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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ipn.escomoto.domain.model.AccessType
import com.ipn.escomoto.domain.model.StatusType
import com.ipn.escomoto.ui.common.ImgFromString
import com.ipn.escomoto.ui.common.SystemFeedbackEffect
import com.ipn.escomoto.ui.components.EnhancedStatusBadge
import com.ipn.escomoto.ui.history.components.MiniStatusIndicator
import com.ipn.escomoto.ui.theme.TextHint
import com.ipn.escomoto.ui.theme.TextHintLight

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
                                // Contenedor de imagen/ícono mejorado
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    ImgFromString(
                                        imageName = "question",
                                        contentDescription = "Blu para salidas y entradas",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        onImageNotFound = {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(64.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                strokeWidth = 6.dp
                                            )
                                        }
                                    )
                                    // Overlay con gradiente sutil
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.5f)
                                                    )
                                                )
                                            )
                                    )
                                    // Ícono de tipo de acceso (pequeño, en esquina)
                                    Surface(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(x = (-4).dp, y = (-4).dp),
                                        shape = CircleShape,
                                        color = Color.Gray.copy(alpha = 0f),
                                        shadowElevation = 2.dp,
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(64.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                strokeWidth = 6.dp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(pendingTitle, style = MaterialTheme.typography.headlineMedium)
                                Text("Esperando aprobación del supervisor...", color = Color.Gray)
                            }

                            StatusType.APPROVED -> {
                                SystemFeedbackEffect(true)
                                // Contenedor de imagen/ícono mejorado
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    ImgFromString(
                                        imageName = if(accessRequest.type == AccessType.ENTRY) "checkin" else "checkout",
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        onImageNotFound = {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(80.dp)
                                            )
                                        }
                                    )
                                    // Overlay con gradiente sutil
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.5f)
                                                    )
                                                )
                                            )
                                    )
                                    // Ícono de tipo de acceso (pequeño, en esquina)
                                    Surface(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(x = (-4).dp, y = (-4).dp),
                                        shape = CircleShape,
                                        color = Color(0xFF4CAF50).copy(alpha = 0.3f),
                                        shadowElevation = 2.dp,
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(approvedTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                Text(approvedSubtitle, style = MaterialTheme.typography.titleMedium)
                            }

                            StatusType.REJECTED -> {
                                SystemFeedbackEffect(false)
                                // Contenedor de imagen/ícono mejorado
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    ImgFromString(
                                        imageName = "rejected",
                                        contentDescription = "Blu para salidas y entradas",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        onImageNotFound = {
                                            Icon(
                                                Icons.Default.Cancel,
                                                null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(80.dp)
                                            )
                                        }
                                    )
                                    // Overlay con gradiente sutil
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.5f)
                                                    )
                                                )
                                            )
                                    )
                                    // Ícono de tipo de acceso (pequeño, en esquina)
                                    Surface(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(x = (-4).dp, y = (-4).dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                                        shadowElevation = 2.dp,

                                        ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text("Solicitud Rechazada", style = MaterialTheme.typography.headlineMedium)
                                Text("Contacta al supervisor.", color = Color.Gray)
                            }

                            StatusType.CANCELLED -> {
                                // Contenedor de imagen/ícono mejorado
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    ImgFromString(
                                        imageName = "surprise",
                                        contentDescription = "Blu para salidas y entradas",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(16.dp)),
                                        onImageNotFound = {
                                            Icon(
                                                Icons.Default.Cancel,
                                                null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(80.dp)
                                            )
                                        }
                                    )
                                    // Overlay con gradiente sutil
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.5f)
                                                    )
                                                )
                                            )
                                    )
                                    // Ícono de tipo de acceso (pequeño, en esquina)
                                    Surface(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.BottomEnd)
                                            .offset(x = (-4).dp, y = (-4).dp),
                                        shape = CircleShape,
                                        color = Color.Gray.copy(alpha = 0.2f),
                                        shadowElevation = 2.dp,

                                        ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
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