package com.example.checkpoint.core.backend.domain.valueobjects

import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType

data class SubscriptionCost(
    private var _cost: Double,
    val type: SubscriptionCostType
) {
    init {
        validateCost(_cost)
    }

    companion object {
        const val ERROR_WRONG_FORMAT = "ERROR_WRONG_FORMAT"
    }

    var cost: Double
        get() = _cost
        set(value) {
            validateCost(value)
            _cost = value
        }

    private fun validateCost(cost: Double) {
        require(cost >= 0.0) { ERROR_WRONG_FORMAT }
    }

}
