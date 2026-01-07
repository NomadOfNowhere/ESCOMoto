package com.ipn.escomoto.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SpawnAttributeForm(
    msgError: String,
    isLastAttribute: Boolean = false
) {
    var value by remember { mutableStateOf("Yamaha") }
    var valueError by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            value = it
            valueError = false
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
        isError = valueError,
        supportingText = {
            if (valueError) {
                Text(msgError)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
    Spacer(modifier = Modifier.height(if(isLastAttribute) 32.dp else 16.dp))
}