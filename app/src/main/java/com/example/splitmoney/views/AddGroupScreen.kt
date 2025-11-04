package com.example.splitmoney.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.splitmoney.viewModels.GroupsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Card
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import com.example.splitmoney.data.SplitMoney
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun AddGroupScreen(viewModel: GroupsViewModel, onBack: () -> Unit) {

    var groupName by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Add New Group", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = groupName,
            onValueChange = {
                groupName = it
            },
            label = { Text("Group Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (groupName.isNotBlank()) {
                    viewModel.addGroup(groupName)
                    onBack()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF42EFC4),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .padding(top = 16.dp)

        ) {
            Text("Add")
        }

        Button(onClick = { onBack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF42EFC4),
                contentColor = Color.Black
            ),
            modifier = Modifier.padding(top = 8.dp)) {
            Text("Cancel")
        }
    }
}
