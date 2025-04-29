package com.example.foodorderingapp.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Extensions and utility functions for the app
 */

/**
 * Formats a Double as a currency string
 * Example: 10.99 -> "$10.99"
 */
fun Double.toCurrencyFormat(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(this)
}

/**
 * Formats a Double as a currency string with the specified currency symbol
 * Example: 10.99, "€" -> "€10.99"
 */
fun Double.toCurrencyFormat(currencySymbol: String): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    format.currency = java.util.Currency.getInstance(currencySymbol)
    return format.format(this)
}