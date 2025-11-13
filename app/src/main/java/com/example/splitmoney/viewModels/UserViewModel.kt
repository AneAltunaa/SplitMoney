package com.example.splitmoney.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repo: UserRepository) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadUsers() {
        viewModelScope.launch { _users.value = repo.getAllUsers() }
    }

    fun registerUser(user: User) = viewModelScope.launch { repo.registerUser(user) }
    fun loginUser(mail: String, password: String) = viewModelScope.launch { repo.loginUser(mail, password) }
    fun getUserById(id: Int) = viewModelScope.launch { repo.getUserById(id) }
    fun updateUser(id: Int, user: User) = viewModelScope.launch { repo.updateUser(id, user) }
    fun deleteUser(id: Int) = viewModelScope.launch { repo.deleteUser(id) }
}

