package com.example.splitmoney.data.repository

import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.network.RetrofitInstance

class UserRepository {
    private val api = RetrofitInstance.api

    suspend fun getAllUsers() = api.getAllUsers()
    suspend fun registerUser(user: User) {
        api.registerUser(
            mapOf(
                "name" to user.name,
                "lastname" to user.lastname,
                "mail" to user.mail,
                "phone" to user.phone,
                "password" to user.password
            )
        )
    }
    suspend fun loginUser(mail: String, password: String) =
        api.loginUser(mapOf("mail" to mail, "password" to password))
    suspend fun getUserById(id: Int) = api.getUserById(id)
    suspend fun getUserByMail(mail: String) = api.getUserByMail(mail)
    suspend fun updateUser(id: Int, user: User) = api.updateUser(id, user)
    suspend fun deleteUser(id: Int) = api.deleteUser(id)

    // トークンをサーバーに送る
    suspend fun updateFcmToken(userId: Int, token: String) =
        api.updateFcmToken(userId, mapOf("token" to token))

    // 催促を送る
    suspend fun sendReminder(targetUserId: Int, groupId: Int) =
        api.sendReminder(mapOf("target_user_id" to targetUserId, "group_id" to groupId))
}

