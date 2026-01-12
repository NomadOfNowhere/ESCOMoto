package com.ipn.escomoto.domain.model
import android.util.Log
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val userType: String = "",
    val escomId: String? = null,
    val officialIdUrl: String? = null,
    @ServerTimestamp
    val createdAt: Date? = null
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
            ║ Identificación: $officialIdUrl
            ╚═══════════════════════════════════════╝
        """.trimIndent()
        Log.d(tag, info)
    }
}