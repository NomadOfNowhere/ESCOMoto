package com.ipn.escomoto.domain.model

import android.net.Uri
import android.util.Log

data class Motorcycle(
    val id: String = "",
    val licensePlate: String = "",
    val model: String = "",
    val image: Uri = Uri.EMPTY,
    val ownerId: String = "",
    val ownerName: String = ""
){
    constructor() : this("","","", Uri.EMPTY, "", "")

    fun debug(title: String = "", tag: String = "USER_DEBUG") {
        val info = """
            ${title}
            ╔════════ DEPURACIÓN DE USUARIO ════════╗
            ║ ID: $id
            ║ Placa: $licensePlate
            ║ Modelo: $model
            ║ Foto: $image
            ║ Dueño: $ownerName
            ║ DueñoID: $ownerId
            ╚═══════════════════════════════════════╝
        """.trimIndent()
        Log.d(tag, info)
    }
}