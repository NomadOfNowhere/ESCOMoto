package com.ipn.escomoto.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Date?.toDateString(): String {
    if (this == null) return "Fecha desconocida"

    // Define el formato (Ej: 10/01/2026 14:30)
    val formatter = SimpleDateFormat("dd/MM/yyyy\nHH:mm", Locale.getDefault())
    return formatter.format(this)
}

// Utilidades para fechas (Colócalas fuera o en DateUtils)
fun getStartOfDay(millis: Long): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}


fun getEndOfDay(millis: Long): Long {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    return calendar.timeInMillis
}