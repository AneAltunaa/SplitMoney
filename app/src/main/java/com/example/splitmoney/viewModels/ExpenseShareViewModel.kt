package com.example.splitmoney.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.ExpenseShare
import com.example.splitmoney.data.repository.ExpenseShareRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExpenseShareViewModel(private val repo: ExpenseShareRepository = ExpenseShareRepository()) : ViewModel() {
    private val _shares = MutableStateFlow<List<ExpenseShare>>(emptyList())
    val shares: StateFlow<List<ExpenseShare>> = _shares

    fun loadSharesByExpense(eid: Int) = viewModelScope.launch { _shares.value = repo.getSharesByExpense(eid) }
    fun addShare(share: ExpenseShare) = viewModelScope.launch { repo.addShare(share) }
    fun updateShare(id: Int, share: ExpenseShare) = viewModelScope.launch { repo.updateShare(id, share) }
    fun deleteShare(id: Int) = viewModelScope.launch { repo.deleteShare(id) }
}
