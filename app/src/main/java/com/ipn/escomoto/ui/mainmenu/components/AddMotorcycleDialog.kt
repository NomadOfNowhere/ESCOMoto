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

@Composable
fun AddMotorcycleDialog(
    onDismiss: () -> Unit,
    onConfirm: (brand: String, model: String, licensePlate: String, imageUri: Uri) -> Unit
) {
    var brand by remember { mutableStateOf("Yamaha") }
    var model by remember { mutableStateOf("R-301") }
    var licensePlate by remember { mutableStateOf("ABC-1234") }
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
                            placeholder = { Text("Ej: ABC-123") },
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
                                    containerColor = MaterialTheme.colorScheme.primary
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
//
//// Actualiza EmptyMotorcycleState para mostrar el dialog
//@Composable
//fun EmptyMotorcycleState(
//    onAddMotorcycle: (brand: String, model: String, licensePlate: String, imageUri: Uri?) -> Unit
//) {
//    var showDialog by remember { mutableStateOf(false) }
//
//    Card(
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        ),
//        shape = RoundedCornerShape(16.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(32.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            // Icono grande
//            Box(
//                modifier = Modifier
//                    .size(80.dp)
//                    .background(
//                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
//                        shape = RoundedCornerShape(40.dp)
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    Icons.Default.DirectionsBike,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(40.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "No tienes motocicletas",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Agrega tu primera motocicleta para comenzar",
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.outline
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Botón con animación
//            var buttonPressed by remember { mutableStateOf(false) }
//            val buttonScale by animateFloatAsState(
//                targetValue = if (buttonPressed) 0.95f else 1f,
//                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
//                label = "button_scale",
//                finishedListener = { buttonPressed = false }
//            )
//
//            Button(
//                onClick = {
//                    buttonPressed = true
//                    showDialog = true
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .scale(buttonScale),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Icon(
//                    Icons.Default.Add,
//                    contentDescription = null,
//                    modifier = Modifier.size(20.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Agregar motocicleta",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//        }
//    }
//
//    // Mostrar dialog cuando se hace clic
//    if (showDialog) {
//        AddMotorcycleDialog(
//            onDismiss = { showDialog = false },
//            onConfirm = { brand, model, licensePlate, imageUri ->
//                showDialog = false
//                onAddMotorcycle(brand, model, licensePlate, imageUri)
//            }
//        )
//    }
//}