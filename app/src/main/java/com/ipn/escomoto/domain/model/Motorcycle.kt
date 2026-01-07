package com.ipn.escomoto.domain.model

import android.net.Uri
import android.util.Log

data class Motorcycle(
    val id: String = "",
    val licensePlate: String = "",
    val brand: String = "",
    val model: String = "",
    val imageUrl: String = "",
    val ownerId: String = "",
    val ownerName: String = ""
){
    fun debug(title: String = "", tag: String = "DEBUG_MOTO") {
        val info = """
            ${title}
            ╔════════ DEPURACIÓN DE USUARIO ════════╗
            ║ ID: $id
            ║ Placa: $licensePlate
            ║ Marca: $brand
            ║ Modelo: $model
            ║ Foto: $imageUrl
            ║ Dueño: $ownerName
            ║ DueñoID: $ownerId
            ╚═══════════════════════════════════════╝
        """.trimIndent()
        Log.d(tag, info)
    }
}