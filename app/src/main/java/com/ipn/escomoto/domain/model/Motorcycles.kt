package com.ipn.escomoto.domain.model

import android.util.Log

data class Motorcycles(
    val id: String = "",
    val licensePlate: String = "",
    val model: String = "",
    val photo: String = "",
    val ownerId: String = "",
    val ownerName: String = ""
){
    constructor() : this("","","", "", "", "")

    fun debug(title: String = "", tag: String = "USER_DEBUG") {
        val info = """
            ${title}
            ╔════════ DEPURACIÓN DE USUARIO ════════╗
            ║ ID: $id
            ║ Placa: $licensePlate
            ║ Modelo: $model
            ║ Foto: $photo
            ║ Dueño: $ownerName
            ║ DueñoID: $ownerId
            ╚═══════════════════════════════════════╝
        """.trimIndent()
        Log.d(tag, info)
    }
}