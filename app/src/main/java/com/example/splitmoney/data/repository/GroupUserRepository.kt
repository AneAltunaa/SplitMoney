package com.example.splitmoney.data.repository

import com.example.splitmoney.data.model.GroupUser
import com.example.splitmoney.data.network.RetrofitInstance

class GroupUserRepository {
    private val api = RetrofitInstance.api

    suspend fun addParticipant(gu: GroupUser) = api.addParticipant(gu)
    suspend fun getParticipants(gid: Int) = api.getParticipants(gid)
    suspend fun deleteParticipant(gu: GroupUser) = api.deleteParticipant(gu)
}
