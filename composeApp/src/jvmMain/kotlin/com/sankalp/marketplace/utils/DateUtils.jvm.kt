package com.sankalp.marketplace.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatMillisToDate(millis: Long): String {
    val date = Date(millis)
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return format.format(date)
}

actual fun getCurrentMillis(): Long = System.currentTimeMillis()