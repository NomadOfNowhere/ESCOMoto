package com.ipn.escomoto.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

fun getTodayRange(): Pair<Date, Date> {
    val zoneId = ZoneId.systemDefault()
    val today = LocalDate.now(zoneId)
    val startInstant = today.atStartOfDay(zoneId).toInstant()
    val endInstant = today.atTime(LocalTime.MAX).atZone(zoneId).toInstant()
    return Pair(Date.from(startInstant), Date.from(endInstant))
}

fun Date?.toDateString(): String {
    if (this == null) return "Fecha desconocida"

    // Define el formato (Ej: 10/01/2026 14:30)
    val formatter = SimpleDateFormat("dd/MM/yyyy\nHH:mm", Locale.getDefault())
    return formatter.format(this)
}

fun getStartOfDayInLocal(utcMillis: Long): Long {
    // Convertimos los milisegundos del Picker (que vienen en UTC) a una FECHA simple
    val instant = Instant.ofEpochMilli(utcMillis)
    val localDate = instant.atZone(ZoneOffset.UTC).toLocalDate()
    val zoneId = ZoneId.systemDefault()
    val startOfDay = localDate.atStartOfDay(zoneId)

    return startOfDay.toInstant().toEpochMilli()
}

fun getEndOfDayInLocal(utcMillis: Long): Long {
    // Convertimos los milisegundos del Picker (que vienen en UTC) a una FECHA simple
    val instant = Instant.ofEpochMilli(utcMillis)
    val localDate = instant.atZone(ZoneOffset.UTC).toLocalDate()
    val zoneId = ZoneId.systemDefault()

    // Calculamos el inicio del día SIGUIENTE y restamos 1 milisegundo
    val endOfDay = localDate.plusDays(1).atStartOfDay(zoneId).minusNanos(1)
    return endOfDay.toInstant().toEpochMilli()
}