package com.example.splitmoney.data

data class SplitMoney(
    val id:String,
    val name:String,
    val description: String,
    val currency: String,
    val members: MutableList<Person> = mutableListOf(),
    val expenses: MutableList<Expense> = mutableListOf()
)
