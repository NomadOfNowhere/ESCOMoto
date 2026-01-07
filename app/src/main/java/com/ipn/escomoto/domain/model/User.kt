package com.ipn.escomoto.domain.model
import android.util.Log

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val escomId: String? = null,
    val userType: String = "",
    val createdAt: Long = System.currentTimeMillis()
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
            ╚═══════════════════════════════════════╝
        """.trimIndent()
        Log.d(tag, info)
    }
}