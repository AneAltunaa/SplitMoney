package com.example.splitmoney.data.model

data class ExpenseShare(
    val id: Int? = null,
    val expense_id: Int,
    val user_id: Int,
    val amount_owed: Double,
    val paid: Int = 0
)
