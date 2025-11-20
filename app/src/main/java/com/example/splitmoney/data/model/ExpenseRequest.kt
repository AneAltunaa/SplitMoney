package com.example.splitmoney.data.model


data class ExpenseShareRequest(
    val user_id: Int,
    val amount_owed: Double
)

data class ExpenseRequest(
    val group_id: Int,
    val description: String,
    val total_amount: Double,
    val paid_by: Int,
    val shares: List<ExpenseShareRequest>
)
