package com.example.splitmoney.data

data class Expense(
    val description: String,
    val amount: Double,
    val paidBy: String,
    val participants: List<Person>
)
