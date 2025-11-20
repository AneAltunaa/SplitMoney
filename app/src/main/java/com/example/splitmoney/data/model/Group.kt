package com.example.splitmoney.data.model

import com.google.gson.annotations.SerializedName

data class Group(
    val id: Int? = null,
    val name: String,
    val description: String,
    @SerializedName("participants")
    val participants: List<Int>? = null  // user id
)
