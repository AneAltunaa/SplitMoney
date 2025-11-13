package com.example.splitmoney.data.repository

import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.data.network.RetrofitInstance

class ExpenseRepository {
    private val api = RetrofitInstance.api

    suspend fun addExpense(expense: Expense) = api.addExpense(expense)
    suspend fun getExpensesByGroup(gid: Int) = api.getExpensesByGroup(gid)
    suspend fun updateExpense(id: Int, expense: Expense) = api.updateExpense(id, expense)
    suspend fun deleteExpense(id: Int) = api.deleteExpense(id)
}
