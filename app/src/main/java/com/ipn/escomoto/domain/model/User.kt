package com.ipn.escomoto.domain.model
import android.util.Log

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val userType: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val escomId: String? = null,
    val officialIdUrl: String? = null,
) {
    fun debug(title: String = "", tag: String = "USER_DEBUG") {
        val info = """
            ${title}
            ╔════════ DEPURACIÓN DE USUARIO ════════╗
            ║ ID: $id
            ║ Nombre: $name
            ║ Email: $email
            ║ Boleta/ID: ${escomId ?: "N/A"}
            ║ Tipo: $userType
            ║ Creado: ${java.util.Date(createdAt)}
            ║ Identificación: $officialIdUrl
            ╚═══════════════════════════════════════╝
        """.trimIndent()
        Log.d(tag, info)
    }
}