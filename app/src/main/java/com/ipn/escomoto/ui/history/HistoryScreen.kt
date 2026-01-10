package com.ipn.escomoto.ui.history

import android.media.Image
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ipn.escomoto.utils.getEndOfDay
import com.ipn.escomoto.utils.getStartOfDay
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import com.ipn.escomoto.ui.components.AnimatedChip
import com.ipn.escomoto.ui.components.JumpRotateIcon
import com.ipn.escomoto.ui.history.components.FilterInputDialog
import com.ipn.escomoto.ui.history.components.HistoryItemCard
import com.ipn.escomoto.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    userRole: String,
    userId: String?,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val logs = viewModel.historyLogs
    val isLoading = viewModel.isLoading
    val filters = viewModel.currentFilter

    // Estados para mostrar diálogos
    var showPlateDialog by remember { mutableStateOf(false) }
    var showUserDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (showFilters) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "icon_rotation"
    )

    // CARGA AUTOMÁTICA AL INICIAR LA PANTALLA
    LaunchedEffect(Unit) {
        if (logs.isEmpty()) { // Solo carga si está vacío para evitar recargas innecesarias al rotar
            viewModel.loadHistory(userRole, userId, filters)
        }
        visible = true
    }

    // Diálogo de Rango de Fechas
    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    // Habilitamos el botón si se seleccionaron ambas fechas (Inicio y Fin)
                    enabled = dateRangePickerState.selectedStartDateMillis != null &&
                            dateRangePickerState.selectedEndDateMillis != null,
                    onClick = {
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis

                        if (startMillis != null && endMillis != null) {
                            // Calculamos inicio del primer día y fin del último día
                            val start = getStartOfDay(startMillis)
                            val end = getEndOfDay(endMillis)

                            // Actualizamos ViewModel
                            val newFilter = filters.copy(startDate = start, endDate = end)
                            viewModel.updateFilter(newFilter)
                            viewModel.loadHistory(userRole, userId, newFilter)
                        }
                        showDatePicker = false
                    }
                ) { Text("Aplicar Rango") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            // Usamos el componente visual de Rango
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Selecciona rango de fechas",
                        modifier = Modifier.padding(16.dp)
                    )
                },
                headline = {
                    // Mostrar las fechas seleccionadas en el encabezado
                    Row(modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)) {
                        val start = dateRangePickerState.selectedStartDateMillis
                        val end = dateRangePickerState.selectedEndDateMillis
                        if (start != null && end != null) {
                            Text("Rango definido")
                        } else {
                            Text("Inicio - Fin")
                        }
                    }
                },
                showModeToggle = false,
                modifier = Modifier.height(500.dp) // Altura recomendada para que se vea bien
            )
        }
    }

    // Diálogos de Texto (Matrícula y Usuario)
    if (showPlateDialog) {
        FilterInputDialog(
            title = "Filtrar por Matrícula",
            onDismiss = { showPlateDialog = false }) { text ->
            val newFilter = filters.copy(licensePlate = text)
            viewModel.updateFilter(newFilter)
            viewModel.loadHistory(userRole, userId, newFilter)
            showPlateDialog = false
        }
    }

    if (showUserDialog) {
        FilterInputDialog(
            title = "Filtrar por ID de Usuario",
            onDismiss = { showUserDialog = false }) { text ->
            val newFilter = filters.copy(userId = text)
            viewModel.updateFilter(newFilter)
            viewModel.loadHistory(userRole, userId, newFilter)
            showUserDialog = false
        }
    }

    PullToRefreshBox(
        modifier = modifier
            .fillMaxSize(),
        isRefreshing = isLoading,
        onRefresh = { viewModel.loadHistory(userRole, userId, filters) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
//                .padding(16.dp)
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            text = "Historial de actividad",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aquí verás tu historial de check-in y check-out",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // BARRA DE FILTROS
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
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (showFilters) PurplePrimary.copy(alpha = 0.1f) else Color.Transparent,
                            modifier = Modifier.size(48.dp),
                            onClick = { showFilters = !showFilters }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                JumpRotateIcon(showOptions = showFilters)
                            }
                        }
                        AnimatedVisibility(
                            visible = showFilters,
                            enter = expandHorizontally(
                                expandFrom = Alignment.Start,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 50,
                                    easing = LinearOutSlowInEasing
                                )
                            ),
                            exit = shrinkHorizontally(
                                shrinkTowards = Alignment.Start,
                                animationSpec = tween(
                                    durationMillis = 250,
                                    easing = FastOutLinearInEasing
                                )
                            ) + fadeOut(
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = LinearOutSlowInEasing
                                )
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                // Filtro Fecha
                                item {
                                    AnimatedChip(
                                        selected = filters.startDate != null,
                                        onClick = { showDatePicker = true },
                                        label = "Fecha",
                                        icon = Icons.Default.CalendarToday,
                                        onClear = if (filters.startDate != null) {
                                            {
                                                val newFilter = filters.copy(startDate = null, endDate = null)
                                                viewModel.updateFilter(newFilter)
                                                viewModel.loadHistory(userRole, userId, newFilter)
                                            }
                                        } else null,
                                        index = 0,
                                        showFilters = showFilters
                                    )
                                }
                                // Filtro Matrícula
                                item {
                                    AnimatedChip(
                                        selected = !filters.licensePlate.isNullOrEmpty(),
                                        onClick = { showPlateDialog = true },
                                        label = if (!filters.licensePlate.isNullOrEmpty()) filters.licensePlate!! else "Matrícula",
                                        icon = Icons.Default.TwoWheeler,
                                        onClear = if (!filters.licensePlate.isNullOrEmpty()) {
                                            {
                                                val newFilter = filters.copy(licensePlate = null)
                                                viewModel.updateFilter(newFilter)
                                                viewModel.loadHistory(userRole, userId, newFilter)
                                            }
                                        } else null,
                                        index = 1,
                                        showFilters = showFilters
                                    )
                                }
                                // Filtro Usuario
                                if (userRole != "ESCOMunidad" && userRole != "Visitante") {
                                        item {
                                            AnimatedChip(
                                                selected = !filters.userId.isNullOrEmpty(),
                                                onClick = { showUserDialog = true },
                                                label = "Usuario",
                                                icon = Icons.Default.Person,
                                                onClear = if (!filters.userId.isNullOrEmpty()) {
                                                    {
                                                        val newFilter = filters.copy(userId = null)
                                                        viewModel.updateFilter(newFilter)
                                                        viewModel.loadHistory(userRole, userId, newFilter)
                                                    }
                                                } else null,
                                                index = 2,
                                                showFilters = showFilters
                                            )
                                        }
                                }
                            }
                        }
                    }
                }
            }

            // ESTADO VACÍO
            if (!isLoading && logs.isEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 300))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp), // Altura fija para centrar visualmente
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontraron registros.", color = Color.Gray)
                        }
                    }
                }
            }

            // LISTA DE LOGS
            items(
                items = logs,
                key = { it.id.ifEmpty { it.hashCode().toString() } }
            ) { log ->
                Column(modifier = Modifier.offset(y = (-8).dp)) {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                                slideInVertically(
                                    initialOffsetY = { 50 },
                                    animationSpec = tween(600, delayMillis = 400)
                                )
                    ) {
                        Column {
                            HistoryItemCard(log)
//                            MotorcycleCard(
//                                moto = moto,
//                                isProcessingCard = showDetailDialog,
//                                onClick = { selectedMotorcycle = moto }
//                            )
                        }
                    }
                }
            }
        }
    }
}