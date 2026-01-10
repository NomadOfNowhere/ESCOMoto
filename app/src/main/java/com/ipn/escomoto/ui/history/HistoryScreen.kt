package com.ipn.escomoto.ui.history

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox // Importante
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.ipn.escomoto.utils.getEndOfDay
import com.ipn.escomoto.utils.getStartOfDay


@OptIn(ExperimentalMaterial3Api::class)
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

    // CARGA AUTOMÁTICA AL INICIAR LA PANTALLA
    LaunchedEffect(Unit) {
        if (logs.isEmpty()) { // Solo carga si está vacío para evitar recargas innecesarias al rotar
            viewModel.loadHistory(userRole, userId, filters)
        }
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
        FilterInputDialog(title = "Filtrar por Matrícula", onDismiss = { showPlateDialog = false }) { text ->
            val newFilter = filters.copy(licensePlate = text)
            viewModel.updateFilter(newFilter)
            viewModel.loadHistory(userRole, userId, newFilter)
            showPlateDialog = false
        }
    }

    if (showUserDialog) {
        FilterInputDialog(title = "Filtrar por ID de Usuario", onDismiss = { showUserDialog = false }) { text ->
            val newFilter = filters.copy(userId = text)
            viewModel.updateFilter(newFilter)
            viewModel.loadHistory(userRole, userId, newFilter)
            showUserDialog = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // BARRA DE FILTROS
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Filtro Fecha
                item {
                    FilterChip(
                        selected = filters.startDate != null,
                        onClick = { showDatePicker = true },
                        label = { Text("Fecha") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                        trailingIcon = if (filters.startDate != null) {
                            {
                                IconButton(onClick = {
                                    val newFilter = filters.copy(startDate = null, endDate = null)
                                    viewModel.updateFilter(newFilter)
                                    viewModel.loadHistory(userRole, userId, newFilter)
                                }, modifier = Modifier.size(16.dp)) { Icon(Icons.Default.Close, null) }
                            }
                        } else null
                    )
                }

                // Filtro Matrícula
                item {
                    FilterChip(
                        selected = !filters.licensePlate.isNullOrEmpty(),
                        onClick = { showPlateDialog = true },
                        label = { Text(if (!filters.licensePlate.isNullOrEmpty()) filters.licensePlate!! else "Matrícula") },
                        leadingIcon = { Icon(Icons.Default.TwoWheeler, null) },
                        trailingIcon = if (!filters.licensePlate.isNullOrEmpty()) {
                            {
                                IconButton(onClick = {
                                    val newFilter = filters.copy(licensePlate = null)
                                    viewModel.updateFilter(newFilter)
                                    viewModel.loadHistory(userRole, userId, newFilter)
                                }, modifier = Modifier.size(16.dp)) { Icon(Icons.Default.Close, null) }
                            }
                        } else null
                    )
                }

                // Filtro Usuario (Solo Admin)
                if (userRole != "ESCOMunidad" && userRole != "Visitante") {
                    item {
                        FilterChip(
                            selected = !filters.userId.isNullOrEmpty(),
                            onClick = { showUserDialog = true },
                            label = { Text("Usuario") },
                            leadingIcon = { Icon(Icons.Default.Person, null) },
                            trailingIcon = if (!filters.userId.isNullOrEmpty()) {
                                {
                                    IconButton(onClick = {
                                        val newFilter = filters.copy(userId = null)
                                        viewModel.updateFilter(newFilter)
                                        viewModel.loadHistory(userRole, userId, newFilter)
                                    }, modifier = Modifier.size(16.dp)) { Icon(Icons.Default.Close, null) }
                                }
                            } else null
                        )
                    }
                }
            }

            // LISTA CON PULL TO REFRESH
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = isLoading,
                onRefresh = { viewModel.loadHistory(userRole, userId, filters) }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isLoading && logs.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No se encontraron registros.", color = Color.Gray)
                            }
                        }
                    }
                    items(logs) { log ->
                        HistoryItemCard(log)
                    }
                }
            }
        }
    }
}