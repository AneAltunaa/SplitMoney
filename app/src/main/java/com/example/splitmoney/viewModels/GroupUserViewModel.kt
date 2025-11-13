package com.example.splitmoney.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.GroupUser
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.repository.GroupUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupUserViewModel(private val repo: GroupUserRepository) : ViewModel() {
    private val _participants = MutableStateFlow<List<User>>(emptyList())
    val participants: StateFlow<List<User>> = _participants

    fun loadParticipants(gid: Int) = viewModelScope.launch { _participants.value = repo.getParticipants(gid) }
    fun addParticipant(gu: GroupUser) = viewModelScope.launch { repo.addParticipant(gu) }
    fun removeParticipant(gu: GroupUser) = viewModelScope.launch { repo.deleteParticipant(gu) }
}
