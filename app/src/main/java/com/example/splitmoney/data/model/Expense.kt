package com.example.splitmoney.data.model

data class Expense(
    val id: Int? = null,
    val group_id: Int,
    val description: String,
    val total_amount: Double,
    val paid_by: Int
)
