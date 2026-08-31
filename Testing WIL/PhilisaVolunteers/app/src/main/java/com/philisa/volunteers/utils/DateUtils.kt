package com.philisa.volunteers.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object DateUtils {

    private val dayMonthYear = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    private val dayMonthYearTime = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        if (timestamp <= 0L) return ""
        return dayMonthYear.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        if (timestamp <= 0L) return ""
        return dayMonthYearTime.format(Date(timestamp))
    }

    fun now(): Long = System.currentTimeMillis()

    /** Generates a human-readable reference number, e.g. APP-2026-4821 (Fig 54). */
    fun generateReferenceNumber(): String {
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val random = Random.nextInt(1000, 9999)
        return "APP-$year-$random"
    }

    /** Generates a short volunteer ID, e.g. VOL-2026-084 (Fig 56). */
    fun generateVolunteerId(): String {
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val random = Random.nextInt(100, 999)
        return "VOL-$year-$random"
    }

    /** Whole months elapsed since [joinedDate], minimum 0. */
    fun monthsSince(joinedDate: Long): Int {
        if (joinedDate <= 0L) return 0
        val diffMillis = now() - joinedDate
        if (diffMillis <= 0) return 0
        val days = diffMillis / (1000L * 60 * 60 * 24)
        return (days / 30).toInt().coerceAtLeast(0)
    }
}
