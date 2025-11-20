package com.example.splitmoney.data.network

import com.example.splitmoney.data.model.Group
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.model.GroupUser
import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.data.model.ExpenseRequest
import com.example.splitmoney.data.model.ExpenseShare
import com.example.splitmoney.data.model.GroupBalancesResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Users
    @GET("users") suspend fun getAllUsers(): List<User>
    @POST("users/register") suspend fun registerUser(@Body data: Map<String, String>): Response<Unit>
    @POST("users/login") suspend fun loginUser(@Body credentials: Map<String, String>): User?
    @GET("users/{id}") suspend fun getUserById(@Path("id") id: Int): User?
    @GET("users/mail/{mail}") suspend fun getUserByMail(@Path("mail") mail: String): User?
    @PUT("users/{id}") suspend fun updateUser(@Path("id") id: Int, @Body user: User): Map<String,String>
    @DELETE("users/{id}") suspend fun deleteUser(@Path("id") id: Int): Map<String,String>

    // Groups
    @POST("groups") suspend fun createGroup(@Body groupData: Group): Map<String, Any>
    @GET("groups/{id}") suspend fun getGroupById(@Path("id") id: Int): Group?
    @GET("groups/user/{uid}") suspend fun getGroupsByUser(@Path("uid") uid: Int): List<Group>
    @PUT("groups/{id}") suspend fun updateGroup(@Path("id") id: Int, @Body group: Group): Map<String,String>
    @DELETE("groups/{id}") suspend fun deleteGroup(@Path("id") id: Int): Map<String,String>

    // GroupUsers
    @POST("group_users") suspend fun addParticipant(@Body gu: GroupUser): Map<String,String>
    @GET("group_users/{gid}") suspend fun getParticipants(@Path("gid") gid: Int): List<User>
    @HTTP(method = "DELETE", path = "group_users", hasBody = true)
    suspend fun deleteParticipant(@Body gu: GroupUser): Map<String,String>

    // Expenses
    @POST("expenses") suspend fun addExpense(@Body expense: ExpenseRequest): Map<String,String>
    @GET("expenses/{gid}") suspend fun getExpensesByGroup(@Path("gid") gid: Int): List<Expense>
    @PUT("expenses/{id}") suspend fun updateExpense(@Path("id") id: Int, @Body expense: Expense): Map<String,String>
    @DELETE("expenses/{id}") suspend fun deleteExpense(@Path("id") id: Int): Map<String,String>

    // ExpenseShares
    @POST("expense_shares") suspend fun addShare(@Body share: ExpenseShare): Map<String,String>
    @GET("expense_shares/{eid}") suspend fun getSharesByExpense(@Path("eid") eid: Int): List<ExpenseShare>
    @PUT("expense_shares/{id}") suspend fun updateShare(@Path("id") id: Int, @Body share: ExpenseShare): Map<String,String>
    @DELETE("expense_shares/{id}") suspend fun deleteShare(@Path("id") id: Int): Map<String,String>
    @GET("groups/{gid}/balances")
    suspend fun getGroupBalances(@Path("gid") gid: Int): GroupBalancesResponse
    @POST("groups/{gid}/settle")
    suspend fun settleGroupDebts(
        @Path("gid") gid: Int,
        @Body body: Map<String, Int> // π.χ. { "user_id": 5 }
    ): Map<String, String>
}
