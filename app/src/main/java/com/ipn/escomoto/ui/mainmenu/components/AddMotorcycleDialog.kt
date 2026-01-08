package com.ipn.escomoto.ui.mainmenu.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import androidx.compose.foundation.BorderStroke
import com.ipn.escomoto.ui.components.FormImagePicker
import com.ipn.escomoto.ui.components.FormTextField
import com.ipn.escomoto.ui.components.GenericFormDialog
/*
@Composable
fun AddMotorcycleDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Uri) -> Unit
) {
    // 1. Estado del Formulario
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Estado de Errores (Map simple o variables booleanas)
    var errors by remember { mutableStateOf(mapOf<String, String>()) }

    // 3. Función de validación
    fun validateAndSubmit() {
        val newErrors = mutableMapOf<String, String>()

        if (brand.isBlank()) newErrors["brand"] = "La marca es requerida"
        if (model.isBlank()) newErrors["model"] = "El modelo es requerido"
        if (plate.isBlank()) newErrors["plate"] = "Las placas son requeridas"
        if (imageUri == null) newErrors["image"] = "La foto es obligatoria"

        if (newErrors.isEmpty()) {
            onConfirm(brand, model, plate, imageUri!!)
        } else {
            errors = newErrors
        }
    }

    // 4. USAMOS EL COMPONENTE GENÉRICO
    GenericFormDialog(
        title = "Agregar motocicleta",
        subtitle = "Completa la información de tu vehículo",
        onDismiss = onDismiss,
        onConfirm = { validateAndSubmit() },
        confirmText = "Agregar"
    ) {
        // --- AQUÍ VA TU CONTENIDO PERSONALIZADO ---

        FormImagePicker(
            imageUri = imageUri,
            onImageSelected = {
                imageUri = it
                errors = errors - "image" // Limpiar error al seleccionar
            },
            errorMessage = errors["image"]
        )

        FormTextField(
            value = brand,
            onValueChange = { brand = it; errors = errors - "brand" },
            label = "Marca",
            placeholder = "Ej: Yamaha",
            icon = Icons.Default.DirectionsBike,
            errorMessage = errors["brand"]
        )

        FormTextField(
            value = model,
            onValueChange = { model = it; errors = errors - "model" },
            label = "Modelo",
            placeholder = "Ej: R-3",
            icon = Icons.Default.Build,
            errorMessage = errors["model"]
        )

        FormTextField(
            value = plate,
            onValueChange = {
                if (it.length <= 10) plate = it.uppercase()
                errors = errors - "plate"
            },
            label = "Placas",
            placeholder = "Ej: ABC-123",
            icon = Icons.Default.Info,
            errorMessage = errors["plate"]
        )
    }
}

*/

@Composable
fun AddMotorcycleDialog(
    onDismiss: () -> Unit,
    onConfirm: (brand: String, model: String, licensePlate: String, imageUri: Uri) -> Unit
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }
    var licensePlateError by remember { mutableStateOf(false) }
    var imageUriError by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(true) }

    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        if (uri != null) {
            imageUriError = false
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) +
                        scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200)) +
                        scaleOut(targetScale = 0.8f, animationSpec = tween(200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Agregar motocicleta",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = {
                                    showDialog = false
                                    onDismiss()
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Completa la información de tu motocicleta",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección de imagen
                        Text(
                            text = "Foto de la motocicleta",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = if (imageUriError) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageUri != null) {
                                    // Mostrar imagen seleccionada
                                    AsyncImage(
                                        model = imageUri,
                                        contentDescription = "Foto de motocicleta",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Botón para cambiar imagen (superpuesto)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f))
                                    )

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Cambiar foto",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    // Placeholder para seleccionar imagen
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.AddAPhoto,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Toca para agregar foto",
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        if (imageUriError) {
                            Text(
                                text = "Debes agregar una foto de la motocicleta",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Campo: Marca
                        OutlinedTextField(
                            value = brand,
                            onValueChange = {
                                brand = it
                                brandError = false
                            },
                            label = { Text("Marca") },
                            placeholder = { Text("Ej: Yamaha, Honda, Suzuki") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.DirectionsBike,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            isError = brandError,
                            supportingText = {
                                if (brandError) {
                                    Text("La marca es requerida")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo: Modelo
                        OutlinedTextField(
                            value = model,
                            onValueChange = {
                                model = it
                                modelError = false
                            },
                            label = { Text("Modelo") },
                            placeholder = { Text("Ej: MT-07, CBR500R") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            isError = modelError,
                            supportingText = {
                                if (modelError) {
                                    Text("El modelo es requerido")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Campo: Placa
                        OutlinedTextField(
                            value = licensePlate,
                            onValueChange = {
                                // Convertir a mayúsculas y limitar longitud
                                if (it.length <= 10) {
                                    licensePlate = it.uppercase()
                                    licensePlateError = false
                                }
                            },
                            label = { Text("Placas") },
                            placeholder = { Text("Ej: ABC123") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            isError = licensePlateError,
                            supportingText = {
                                if (licensePlateError) {
                                    Text("Las placas son requeridas")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Botones
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            var cancelPressed by remember { mutableStateOf(false) }
                            var confirmPressed by remember { mutableStateOf(false) }

                            val cancelScale by animateFloatAsState(
                                targetValue = if (cancelPressed) 0.95f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "cancel_scale",
                                finishedListener = { cancelPressed = false }
                            )

                            val confirmScale by animateFloatAsState(
                                targetValue = if (confirmPressed) 0.95f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "confirm_scale",
                                finishedListener = { confirmPressed = false }
                            )

                            // Botón Cancelar
                            OutlinedButton(
                                onClick = {
                                    cancelPressed = true
                                    showDialog = false
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .scale(cancelScale),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Cancelar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Botón Agregar
                            Button(
                                onClick = {
                                    // Validar campos
                                    var isValid = true

                                    if (brand.isBlank()) {
                                        brandError = true
                                        isValid = false
                                    }
                                    if (model.isBlank()) {
                                        modelError = true
                                        isValid = false
                                    }
                                    if (licensePlate.isBlank()) {
                                        licensePlateError = true
                                        isValid = false
                                    }

                                    if (imageUri == null) {
                                        imageUriError = true
                                        isValid = false
                                    }

                                    if (isValid) {
                                        confirmPressed = true
                                        onConfirm(brand, model, licensePlate, imageUri!!)
                                        showDialog = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .scale(confirmScale),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7B68EE)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Agregar",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
