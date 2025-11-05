package com.example.splitmoney.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.splitmoney.data.SplitMoney

class GroupsViewModel : ViewModel(){
    private val _groups = mutableStateListOf<SplitMoney>()
    val groups: List<SplitMoney> = _groups



    fun addGroup(name : String) {
        if (name.isNotBlank()) {
            _groups.add(
                SplitMoney(
                    id = (_groups.size + 1).toString(),
                    name = name,
                    members = mutableListOf()
                )
            )
        }
    }
}