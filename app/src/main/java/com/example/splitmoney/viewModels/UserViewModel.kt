package com.example.splitmoney.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repo: UserRepository = UserRepository()) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _foundUser = MutableStateFlow<User?>(null)
    val foundUser: StateFlow<User?> = _foundUser

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun clearFoundUser() {
        _foundUser.value = null
    }

    fun findUserByEmail(mail: String) = viewModelScope.launch {
        _foundUser.value = try { repo.getUserByMail(mail) } catch (e: Exception) { null }
    }

    fun loadUsers() = viewModelScope.launch { _users.value = repo.getAllUsers() }

    fun loadUserById(id: Int) = viewModelScope.launch {
        _currentUser.value = try { repo.getUserById(id) } catch (e: Exception) { null }
    }

    fun registerUser(user: User) = viewModelScope.launch { repo.registerUser(user) }
    fun loginUser(mail: String, password: String) = viewModelScope.launch { repo.loginUser(mail, password) }
    fun updateUser(id: Int, user: User) = viewModelScope.launch { repo.updateUser(id, user) }
    fun deleteUser(id: Int) = viewModelScope.launch { repo.deleteUser(id) }
}
