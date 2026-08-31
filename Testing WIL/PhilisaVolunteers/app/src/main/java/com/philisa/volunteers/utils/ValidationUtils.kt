package com.philisa.volunteers.utils

import android.util.Patterns

object ValidationUtils {

    fun isNotBlank(value: String?): Boolean = !value.isNullOrBlank()

    fun isValidEmail(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        return Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()
    }

    fun isValidPhone(value: String?): Boolean {
        if (value.isNullOrBlank()) return false
        val digitsOnly = value.filter { it.isDigit() }
        return digitsOnly.length in 9..15
    }

    fun isValidSpotsCount(value: String?): Boolean {
        val n = value?.toIntOrNull() ?: return false
        return n > 0
    }
}
