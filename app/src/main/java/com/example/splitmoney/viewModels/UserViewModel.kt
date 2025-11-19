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

    private val _loggedUserId = MutableStateFlow<Int?>(null)
    val loggedUserId: StateFlow<Int?> = _loggedUserId

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private var pendingFcmToken: String? = null

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

            // ログイン成功、かつ保留中のトークンがある場合、サーバーに送信！
            if (user != null && user.id != null && pendingFcmToken != null) {
                android.util.Log.d("FCM", "Login successful. Sending pending token.")
                registerFcmToken(pendingFcmToken!!)
            }

        } catch (e: Exception) {
            _loginError.value = "Login failed: ${e.message}"
        }
    }

    fun updateUser(id: Int, user: User) = viewModelScope.launch { repo.updateUser(id, user) }
    fun deleteUser(id: Int) = viewModelScope.launch { repo.deleteUser(id) }

    // トークン登録処理
    // ★変更3: トークンを受け取ったら変数に保存し、ログイン済みなら即送信
    fun registerFcmToken(token: String) {
        // 常に最新のトークンを保持しておく
        pendingFcmToken = token

        val userId = _loggedUserId.value
        if (userId != null) {
            viewModelScope.launch {
                try {
                    repo.updateFcmToken(userId, token)
                    android.util.Log.d("FCM", "Success: Token sent for user $userId")
                } catch (e: Exception) {
                    android.util.Log.e("FCM", "Failed to send token: ${e.message}")
                }
            }
        } else {
            android.util.Log.d("FCM", "Token saved locally (User not logged in yet)")
        }
    }

    // 催促処理
    fun sendReminderNotification(targetUserId: Int, groupId: Int) = viewModelScope.launch {
        try {
            repo.sendReminder(targetUserId, groupId)
            android.util.Log.d("FCM", "Reminder sent successfully")
        } catch (e: Exception) {
            android.util.Log.e("FCM", "Failed to send reminder: ${e.message}")
        }
    }
}
