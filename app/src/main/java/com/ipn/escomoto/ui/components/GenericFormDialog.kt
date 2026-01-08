package com.ipn.escomoto.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun GenericFormDialog(
    title: String,
    subtitle: String? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean = false, // Opcional: para mostrar spinner en el botón
    confirmText: String = "Guardar",
    cancelText: String = "Cancelar",
    content: @Composable () -> Unit // <--- Aquí inyectamos los campos
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.8f, animationSpec = tween(200))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    // --- HEADER ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = subtitle,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- CONTENIDO DEL FORMULARIO (SLOT) ---
                    content()

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- FOOTER (BOTONES) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Botón Cancelar
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(cancelText)
                        }

                        // Botón Confirmar
                        Button(
                            onClick = onConfirm,
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7B68EE)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(confirmText)
                        }
                    }
                }
            }
        }
    }
}