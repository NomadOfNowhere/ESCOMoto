package com.ipn.escomoto.data.repository

import com.google.firebase.firestore.FirebaseFirestoreException

fun Exception.toUserFriendlyMessage(): String {
    if (this is FirebaseFirestoreException) {
        return when (this.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "Acceso denegado: El sistema está en mantenimiento, fuera de horario o no tienes permisos."

            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "Sin conexión a internet."

            FirebaseFirestoreException.Code.NOT_FOUND ->
                "Error: El recurso solicitado no existe."

            FirebaseFirestoreException.Code.ALREADY_EXISTS ->
                "Error: Este registro ya existe."

            else -> "Error de base de datos: ${this.message}"
        }
    }
    return "Error desconocido: ${this.localizedMessage}"
}