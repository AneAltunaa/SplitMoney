package com.example.splitmoney.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.Group
import com.example.splitmoney.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel(
    private val repo: GroupRepository = GroupRepository()
) : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _createdGroupId = MutableStateFlow<Int?>(null)
    val createdGroupId: StateFlow<Int?> = _createdGroupId

    fun loadGroupsByUser(uid: Int) = viewModelScope.launch {
        _groups.value = repo.getGroupsByUser(uid)
    }

    fun createGroup(group: Group, onSuccess: (Int) -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
        try {
            val res = repo.createGroup(group)
            val id = (res["group_id"] as? Double)?.toInt()
                ?: throw Exception("Server didnt send 'group_id'. Response: $res")
            _createdGroupId.value = id
            onSuccess(id)
        } catch (e: Exception) {
            onError(e.message ?: "Error")
        }
    }

    fun getGroupById(id: Int) = viewModelScope.launch { repo.getGroupById(id) }
    fun updateGroup(id: Int, group: Group) = viewModelScope.launch { repo.updateGroup(id, group) }
    fun deleteGroup(id: Int) = viewModelScope.launch { repo.deleteGroup(id) }
}


