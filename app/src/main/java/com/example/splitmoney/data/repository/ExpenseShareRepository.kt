package com.example.splitmoney.data.repository

import com.example.splitmoney.data.model.ExpenseShare
import com.example.splitmoney.data.network.RetrofitInstance

class ExpenseShareRepository {
    private val api = RetrofitInstance.api

    suspend fun addShare(share: ExpenseShare) = api.addShare(share)
    suspend fun getSharesByExpense(eid: Int) = api.getSharesByExpense(eid)
    suspend fun updateShare(id: Int, share: ExpenseShare) = api.updateShare(id, share)
    suspend fun deleteShare(id: Int) = api.deleteShare(id)
    suspend fun sendReminder(id: Int) = api.sendReminder(id)
}
