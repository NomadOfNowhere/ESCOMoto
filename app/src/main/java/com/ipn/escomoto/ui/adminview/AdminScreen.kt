package com.ipn.escomoto.ui.adminview

import androidx.compose.material3.ripple
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipn.escomoto.ui.adminview.components.StyledMasterControl
import com.ipn.escomoto.ui.adminview.components.SystemStatsCard

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val stats = viewModel.stats
    val isLoading = viewModel.isLoading
    var visible by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            visible = true
        }
    }

    if (showSearchDialog) {
        UserSearchDialog(
            userlist = viewModel.userProfiles,
            onDismiss = { showSearchDialog = false },
            onUserSelected = { userId ->
                viewModel.setUserType(userId, "Supervisor")
                showSearchDialog = false
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadStats() }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // HEADER
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600)) +
                                slideInVertically(initialOffsetY = { -40 })
                    ) {
                        Column {
                            Text(
                                text = "Detalles del sistema",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Promover a supervisor
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                    ) {
                        Text(
                            text = "Gestión de supervisores",
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
                        Column {
                            // Tarjeta de Acción
                            Card(
                                onClick = { showSearchDialog = true }, // Variable de estado que crearemos abajo
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Nombrar Supervisor",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Busca un empleado y asígnale permisos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                    Icon(
                                        Icons.Default.PersonSearch,
                                        contentDescription = "Buscar usuario",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .padding(start = 16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Switches
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                    ) {
                        Text(
                            text = "Estado del sistema",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
                if (viewModel.isSettingsLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    item {
                        StyledMasterControl(
                            visible = visible,
                            systemEnabled = viewModel.systemSettings.systemEnabled,
                            checksEnabled = viewModel.systemSettings.checksEnabled,
                            onSystemToggle = { viewModel.toggleSystem(it) },
                            onChecksToggle = { viewModel.toggleChecks(it) }
                        )
                    }
                }

                // Estadísticas del sistema
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                    ) {
                        Text(
                            text = "Estadísticas del sistema",
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
                        SystemStatsCard(stats)
                    }
                }
            }
        }
        if (isLoading && !visible) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}