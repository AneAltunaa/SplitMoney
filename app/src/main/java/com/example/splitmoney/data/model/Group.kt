package com.example.splitmoney.data.model

data class Group(
    val id: Int? = null,
    val name: String,
    val description: String,
    val participants: List<Int>? = null  // user id
)
