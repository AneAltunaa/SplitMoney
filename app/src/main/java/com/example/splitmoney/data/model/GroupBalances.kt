package com.example.splitmoney.data.model
/*
data class NetBalance(
    val user_id: Int,
    val name: String,
    val balance: Double
)

data class Settlement(
    val from: Int,
    val to: Int,
    val amount: Double
)

data class GroupBalancesResponse(
    val net_balances: List<NetBalance>,
    val settlements: List<Settlement>
)
*/
data class GroupBalancesResponse(
    val net_balances: List<NetBalance>,
    val settlements: List<Settlement>
)

data class NetBalance(
    val user_id: Int,
    val name: String,
    val balance: Double
)

data class Settlement(
    val from: Int,
    val to: Int,
    val amount: Double
)
