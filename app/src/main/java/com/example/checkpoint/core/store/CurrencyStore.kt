package com.example.checkpoint.core.store

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class Currency(val symbol: String, val position: String) {
    USD("$", "before"),
    EUR("€", "after"),
    GBP("£", "before")
}

object CurrencyStore {
    private val _selectedCurrency = MutableStateFlow(Currency.USD)
    val selectedCurrency: StateFlow<Currency> = _selectedCurrency

    fun setCurrency(currency: Currency) {
        _selectedCurrency.value = currency
    }

    fun getCurrencySymbol(): String = _selectedCurrency.value.symbol
    private fun getCurrencyPosition(): String = _selectedCurrency.value.position

    fun formatPrice(amount: String): String {
        val symbol = getCurrencySymbol()
        val position = getCurrencyPosition()

        val formattedAmount = try {
            "%.2f".format(amount.toDouble())
        } catch (e: NumberFormatException) {
            amount
        }
        return if (position == "before") "$symbol$formattedAmount" else "$formattedAmount$symbol"
    }
}
