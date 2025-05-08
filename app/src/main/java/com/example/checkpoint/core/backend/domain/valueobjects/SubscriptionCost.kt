package com.example.checkpoint.core.backend.domain.valueobjects

import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import java.math.BigDecimal
import java.math.RoundingMode

data class SubscriptionCost(
    private var _cost: Double,
    val type: SubscriptionCostType
) {
    init {
        validateCost(_cost)
    }

    companion object {
        const val ERROR_WRONG_FORMAT = "ERROR_WRONG_FORMAT"
        const val ERROR_WRONG_FORMAT_POINT = "ERROR_WRONG_FORMAT_POINT"
        const val ERROR_TOO_BIG = "ERROR_TOO_BIG"
        val MAX_COST = BigDecimal("9999999.99")
        private val COST_REGEX = Regex("^\\d+(\\.\\d{1,2})?$")
    }

    var cost: Double
        get() = _cost
        set(value) {
            validateCost(value)
            _cost = value
        }

    private fun validateCost(cost: Double) {
        val costValue = BigDecimal(cost).setScale(2, RoundingMode.HALF_UP)
        require(costValue >= BigDecimal.ZERO) { ERROR_WRONG_FORMAT }
        require(costValue <= MAX_COST) { ERROR_TOO_BIG }
        require(COST_REGEX.matches(costValue.toPlainString())) { ERROR_WRONG_FORMAT_POINT }

    }
}
