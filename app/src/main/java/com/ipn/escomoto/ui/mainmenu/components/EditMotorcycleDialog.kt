package com.ipn.escomoto.ui.mainmenu.components

import com.ipn.escomoto.ui.components.FormImagePicker
import com.ipn.escomoto.ui.components.FormTextField
import com.ipn.escomoto.ui.components.GenericFormDialog
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Info
import com.ipn.escomoto.domain.model.Motorcycle

@Composable
fun EditMotorcycleDialog(
    motorcycle: Motorcycle,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Uri?) -> Unit // Uri? puede ser nulo si no cambió la foto
) {
    // 1. Inicializamos con los datos EXISTENTES
    var brand by remember { mutableStateOf(motorcycle.brand) }
    var model by remember { mutableStateOf(motorcycle.model) }
    var plate by remember { mutableStateOf(motorcycle.licensePlate) }

    // imageUri inicia en null. Si sigue null al guardar, significa "no cambies la foto"
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var errors by remember { mutableStateOf(mapOf<String, String>()) }

    fun validateAndSubmit() {
        val newErrors = mutableMapOf<String, String>()

        if (brand.isBlank()) newErrors["brand"] = "La marca es requerida"
        if (model.isBlank()) newErrors["model"] = "El modelo es requerido"
        if (plate.isBlank()) newErrors["plate"] = "Las placas son requeridas"

        // NOTA: No validamos imagen aquí. Si es null, usamos la vieja.

        if (newErrors.isEmpty()) {
            onConfirm(brand, model, plate, imageUri)
        } else {
            errors = newErrors
        }
    }

    GenericFormDialog(
        title = "Editar motocicleta",
        subtitle = "Modifica los datos de tu vehículo",
        onDismiss = onDismiss,
        onConfirm = { validateAndSubmit() },
        confirmText = "Guardar"
    ) {
        // --- Selector de Imagen Inteligente ---
        // Si no se ha elegido nueva imagen (imageUri == null), mostramos un texto indicando que hay una actual
        FormImagePicker(
            imageUri = imageUri,
            onImageSelected = {
                imageUri = it
            },
            // Hack visual: Si no hay URI nueva, pasamos un error null,
            // pero podrías modificar FormImagePicker para mostrar la URL vieja.
            // Por simplicidad, aquí solo mostramos el picker estándar.
            errorMessage = null
        )

        FormTextField(
            value = brand,
            onValueChange = { brand = it },
            label = "Marca",
            icon = Icons.Default.DirectionsBike,
            errorMessage = errors["brand"]
        )

        FormTextField(
            value = model,
            onValueChange = { model = it },
            label = "Modelo",
            icon = Icons.Default.Build,
            errorMessage = errors["model"]
        )

        FormTextField(
            value = plate,
            onValueChange = { if (it.length <= 10) plate = it.uppercase() },
            label = "Placas",
            icon = Icons.Default.Info,
            errorMessage = errors["plate"]
        )
    }
}