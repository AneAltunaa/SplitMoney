package com.example.splitmoney.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.data.model.ExpenseRequest
import com.example.splitmoney.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val repo: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    fun loadExpensesByGroup(gid: Int) = viewModelScope.launch {
        _expenses.value = repo.getExpensesByGroup(gid)
    }

    // ΤΩΡΑ παίρνει ExpenseRequest, όχι Expense
    fun addExpense(request: ExpenseRequest) = viewModelScope.launch {
        repo.addExpense(request)
        // προαιρετικά:
        // loadExpensesByGroup(request.group_id)
    }

    fun updateExpense(id: Int, expense: Expense) = viewModelScope.launch {
        repo.updateExpense(id, expense)
    }

    fun deleteExpense(groupId: Int, expenseId: Int) = viewModelScope.launch {
        repo.deleteExpense(expenseId)
        loadExpensesByGroup(groupId)
    }
}