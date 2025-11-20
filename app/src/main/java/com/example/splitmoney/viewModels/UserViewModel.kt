package com.example.splitmoney.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

class UserViewModel(private val repo: UserRepository = UserRepository()) : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _foundUser = MutableStateFlow<User?>(null)
    val foundUser: StateFlow<User?> = _foundUser

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _loggedUserId = MutableStateFlow<Int?>(null)
    val loggedUserId: StateFlow<Int?> = _loggedUserId

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

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

    fun register(name: String, surname: String, email: String, phone: String, password: String) =
        viewModelScope.launch {
            repo.registerUser(
                User(
                    id = null,
                    name = name,
                    lastname = surname,
                    mail = email,
                    phone = phone,
                    password = password
                )
            )
        }

    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            val user = repo.loginUser(email, password)
            _loggedUserId.value = user?.id
            _loginError.value = if (user == null) "Invalid credentials" else null
        } catch (e: Exception) {
            _loginError.value = "Login failed: ${e.message}"
        }
    }

    fun updateUser(id: Int, user: User) = viewModelScope.launch { repo.updateUser(id, user) }
    fun deleteUser(id: Int) = viewModelScope.launch { repo.deleteUser(id) }

    fun registerFcmToken(userId: Int) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM", "Token retrieved: $token")

            // Send token to backend
            viewModelScope.launch {
                try {
                    // repositoryに定義したメソッドを呼び出す
                    // ※ ご自身のUserRepositoryの定義に合わせて repo.updateFcmToken を呼んでください
                    // UserViewModel内で repository が private val repo で宣言されている前提です
                    repo.updateFcmToken(userId, token)
                    Log.d("FCM", "Token sent to server successfully")
                } catch (e: Exception) {
                    Log.e("FCM", "Failed to send token to server", e)
                }
            }
        }
    }
}
