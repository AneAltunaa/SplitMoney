package com.example.splitmoney.data

data class SplitMoney(
    val id:String,
    val name:String,
    val members: MutableList<String> = mutableListOf(),
    val expenses: MutableList<Expense> = mutableListOf()
)
