package com.example.splitmoney.data.repository

import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.network.RetrofitInstance

class UserRepository {
    private val api = RetrofitInstance.api

    suspend fun getAllUsers() = api.getAllUsers()
    suspend fun registerUser(user: User) = api.registerUser(user)
    suspend fun loginUser(mail: String, password: String) =
        api.loginUser(mapOf("mail" to mail, "password" to password))
    suspend fun getUserById(id: Int) = api.getUserById(id)
    suspend fun getUserByMail(mail: String) = api.getUserByMail(mail)
    suspend fun updateUser(id: Int, user: User) = api.updateUser(id, user)
    suspend fun deleteUser(id: Int) = api.deleteUser(id)
}

