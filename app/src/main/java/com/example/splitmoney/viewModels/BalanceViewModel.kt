package com.example.splitmoney.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.GroupBalancesResponse
import com.example.splitmoney.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BalanceViewModel(
    private val groupRepo: GroupRepository = GroupRepository()
) : ViewModel() {

    private val _balances = MutableStateFlow<GroupBalancesResponse?>(null)
    val balances: StateFlow<GroupBalancesResponse?> = _balances

    fun loadBalances(groupId: Int) = viewModelScope.launch {
        _balances.value = groupRepo.getGroupBalances(groupId)
    }
}