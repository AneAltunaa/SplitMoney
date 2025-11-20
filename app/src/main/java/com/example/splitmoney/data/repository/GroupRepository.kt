package com.example.splitmoney.data.repository

import com.example.splitmoney.data.model.Group
import com.example.splitmoney.data.model.GroupBalancesResponse
import com.example.splitmoney.data.network.RetrofitInstance

class GroupRepository {
    private val api = RetrofitInstance.api

    suspend fun createGroup(group: Group) = api.createGroup(group)
    suspend fun getGroupById(id: Int) = api.getGroupById(id)
    suspend fun getGroupsByUser(uid: Int) = api.getGroupsByUser(uid)
    suspend fun updateGroup(id: Int, group: Group) = api.updateGroup(id, group)
    suspend fun deleteGroup(id: Int) = api.deleteGroup(id)


    suspend fun getGroupBalances(groupId: Int): GroupBalancesResponse =
        api.getGroupBalances(groupId)
}