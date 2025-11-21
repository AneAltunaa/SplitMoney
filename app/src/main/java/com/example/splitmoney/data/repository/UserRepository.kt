package com.example.splitmoney.data.repository

import android.util.Log
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

    /**
     * Obravnava prijavo. Zaradi načina delovanja Flask backenda,
     * kjer neuspešna prijava (ne najde se uporabnik) vrne JSON objekt
     * z napako (HTTP 200 OK), Retrofit to parsira kot objekt User z ID = null.
     * Zato moramo izrecno preveriti, ali je ID prisoten.
     */
    suspend fun loginUser(mail: String, password: String): User? {
        val user = api.loginUser(mapOf("mail" to mail, "password" to password))

        // Če je uporabnik najden in ima ID (kar se zgodi samo ob uspešni prijavi), vrni uporabnika.
        return if (user != null && user.id != null) {
            user
        } else {
            // Če je bil vrnjen objekt z null ID-jem (neuspešna prijava), vrni null.
            Log.w("REPO_FLOW", "Login failed: Backend returned object with null ID or null object.")
            null
        }
    }

    suspend fun getUserById(id: Int) = api.getUserById(id)
    suspend fun getUserByMail(mail: String) = api.getUserByMail(mail)
    suspend fun updateUser(id: Int, user: User) = api.updateUser(id, user)
    suspend fun deleteUser(id: Int) = api.deleteUser(id)
    suspend fun updateFcmToken(userId: Int, token: String) {
        api.updateFcmToken(userId, mapOf("token" to token))
    }
}