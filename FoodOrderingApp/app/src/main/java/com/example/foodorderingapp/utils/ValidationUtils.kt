package com.example.foodorderingapp.utils

import android.util.Patterns
import java.util.regex.Pattern

/**
 * Validates email format using Android's Patterns
 */
fun validateEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/**
 * Validates password length (minimum 6 characters)
 */
fun validatePassword(password: String): Boolean {
    return password.length >= 6
}

/**
 * Validates phone number format
 * Accepts formats: +1234567890, 1234567890, 123-456-7890
 */
fun validatePhone(phone: String): Boolean {
    val phonePattern = Pattern.compile(
        "^\\+?[0-9]{10,15}$|" +  // +1234567890 or 1234567890 (10-15 digits)
        "^[0-9]{3}-[0-9]{3}-[0-9]{4}$"  // 123-456-7890
    )
    return phonePattern.matcher(phone).matches()
}