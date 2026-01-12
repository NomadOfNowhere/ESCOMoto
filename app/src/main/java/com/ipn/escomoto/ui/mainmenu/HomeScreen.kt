package com.ipn.escomoto.ui.mainmenu

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ipn.escomoto.domain.model.AccessRequest
import com.ipn.escomoto.domain.model.Motorcycle
import com.ipn.escomoto.ui.common.SnackbarType
import com.ipn.escomoto.ui.mainmenu.components.AddMotorcycleCard
import com.ipn.escomoto.ui.mainmenu.components.AddMotorcycleDialog
import com.ipn.escomoto.ui.mainmenu.components.EditMotorcycleDialog
import com.ipn.escomoto.ui.mainmenu.components.LoadingSkeletonCard
import com.ipn.escomoto.ui.mainmenu.components.MotorcycleCard
import com.ipn.escomoto.ui.mainmenu.components.MotorcycleDetailDialog
import com.ipn.escomoto.ui.mainmenu.components.PendingRequestCard
import com.ipn.escomoto.ui.mainmenu.components.QuickActionCard
import com.ipn.escomoto.ui.mainmenu.components.SelectMotorcycleDialog

@Composable
fun HomeScreen(
    // Datos básicos
    name: String,
    escomId: String,
    userType: String,
    email: String,
    modifier: Modifier,

    // Estado de datos
    motorcycles: List<Motorcycle>,
    isLoading: Boolean,
    isUserInside: Boolean,
    updatingMotorcycleId: String?,
    showMotoSelector: Boolean,
    pendingRequests: List<AccessRequest> = emptyList(),

    // Acciones
    onRefresh: () -> Unit,
    onAddMotorcycle: (String, String, String, Uri) -> Unit,
    onUpdateMotorcycle: (Motorcycle, String, String, String, Uri?) -> Unit,
    onDeleteMotorcycle: (String) -> Unit,
    onCheckInTap: () -> Unit,
    onCheckOutTap: () -> Unit,
    onMotoSelected: (Motorcycle) -> Unit,
    onDismissSelector: () -> Unit,
    onApproveRequest: (AccessRequest) -> Unit = {},
    onRejectRequest: (AccessRequest) -> Unit = {}
) {
    // Animación de entrada para elementos
    var visible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Estado para edición
    var selectedMotorcycle by remember { mutableStateOf<Motorcycle?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showDetailDialog = selectedMotorcycle != null && !showEditDialog && !showDeleteConfirmation

    // Diálogo: Detalle de moto
    if (showDetailDialog) {
        MotorcycleDetailDialog(
            motorcycle = selectedMotorcycle!!,
            onDismiss = { selectedMotorcycle = null },
            onEdit = { showEditDialog = true },
            onDelete = { showDeleteConfirmation = true }
        )
    }

    // Selector de Moto
    if (showMotoSelector) {
        SelectMotorcycleDialog(
            motorcycles = motorcycles,
            isProcessing = showMotoSelector,
            onDismiss = onDismissSelector,
            onMotoSelected = onMotoSelected
        )
    }

    if (showEditDialog && selectedMotorcycle != null) {
        EditMotorcycleDialog(
            motorcycle = selectedMotorcycle!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { brand, model, plate, newUri ->
                onUpdateMotorcycle(selectedMotorcycle!!, brand, model, plate, newUri)
                showEditDialog = false
                selectedMotorcycle = null
            }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("¿Eliminar motocicleta?") },
            text = { Text("Esta acción no se puede deshacer. ¿Estás seguro?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Borramos usando el ID
                        onDeleteMotorcycle(selectedMotorcycle!!.id)
                        showDeleteConfirmation = false
                        selectedMotorcycle = null // Cerramos el detalle
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDialog) {
        AddMotorcycleDialog(
            onDismiss = { showDialog = false },
            onConfirm = { brand, model, plate, imageUri ->
                onAddMotorcycle(brand, model, plate, imageUri)
                showDialog = false
            }
        )
    }

    LaunchedEffect(Unit) {
        visible = true
    }


    // eii
    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        )
        {
            item {
                // Header con saludo - Animación de fade in y slide
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600)) +
                            slideInVertically(initialOffsetY = { -40 })
                ) {
                    Column {
                        Text(
                            text = "¡Hola, ${name.split(" ")[0]}!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when(userType) {
                                "ESCOMunidad" -> "Número de boleta/empleado: ${escomId}"
                                "Supervisor","Administrador" -> "${userType} con ID: ${escomId}"
                                else -> "Visitante, tu plan es temporal"
                            },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }

            // Sección según tipo de usuario
            if (userType == "Supervisor" || userType == "Administrador") {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                    ) {
                        Text(
                            text = "Solicitudes pendientes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
                if (pendingRequests.isEmpty()) {
                    item {
                        // Estado Vacío para Supervisor
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No hay solicitudes pendientes", color = Color.Gray)
                            }
                        }
                    }
                }
                else {
                    // Lista de Tarjetas
                    items(pendingRequests, key = { it.id }) { request ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                                    slideInVertically(
                                        initialOffsetY = { 50 },
                                        animationSpec = tween(600, delayMillis = 400)
                                    )
                        ) {
                            Column {
                                PendingRequestCard(
                                    request = request,
                                    onApprove = { onApproveRequest(request) },
                                    onReject = { onRejectRequest(request) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 100))
                ) {
                    Text(
                        text = "Acciones rápidas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 200)
                            )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionCard(
                            icon = Icons.Default.Login,
                            title = "Check-in",
                            // color = Color(0xFF7C4DFF),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                            onClick = onCheckInTap
                        )
                        QuickActionCard(
                            icon = Icons.Default.Logout,
                            title = "Check-out",
                            color = Color(0xFFFF6E40),
                            modifier = Modifier.weight(1f),
                            onClick = onCheckOutTap
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                ) {
                    Text(
                        text = "Mis motocicletas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
            items(
                items = motorcycles,
                key = { it.id.ifEmpty { it.hashCode().toString() } }
            ) { moto ->
                Column {
                    if(moto.id == updatingMotorcycleId) {
                        LoadingSkeletonCard()
                    }
                    else {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                                    slideInVertically(
                                        initialOffsetY = { 50 },
                                        animationSpec = tween(600, delayMillis = 400)
                                    )
                        ) {
                            Column {
                                MotorcycleCard(
                                    moto = moto,
                                    isProcessingCard = showDetailDialog,
                                    onClick = { selectedMotorcycle = moto }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                            slideInVertically(
                                initialOffsetY = { 50 },
                                animationSpec = tween(600, delayMillis = 400)
                            )
                ) {
                    Column {
                        LoadingSkeletonCard()
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            val canAddMore = (userType != "Visitante" && motorcycles.size < 3) ||
                    (userType == "Visitante" && motorcycles.isEmpty())

            if (canAddMore) {
                item {
                    AnimatedVisibility(
                        visible = !isLoading && visible,
                        enter = fadeIn(animationSpec = tween(500, delayMillis = 400)) +
                                slideInVertically(
                                    initialOffsetY = { 50 },
                                    animationSpec = tween(500, delayMillis = 400)
                                ),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        AddMotorcycleCard(
                            isListEmpty = motorcycles.isEmpty(),
                            isProcessing = showDialog,
                            onAddMotorcycle = { showDialog = true }
                        )
                    }
                }
            }
        }
    }
}