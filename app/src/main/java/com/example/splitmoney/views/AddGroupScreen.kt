package com.example.splitmoney.views

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.splitmoney.viewModels.GroupsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.TextFieldDefaults
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


import com.example.splitmoney.data.SplitMoney
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column

import androidx.compose.material.icons.filled.Settings

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.TextField
import androidx.wear.compose.material.Colors



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import com.example.splitmoney.data.Person

@Composable
fun CurrencySelector(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    currencies: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()
        .padding(top = 10.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF42EFC4), shape = RoundedCornerShape(20.dp))
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp )
                .clickable { expanded = !expanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedCurrency,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Currency",
                    tint = Color(0xFF42EFC4)
                )
            }
        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
            .border(
                width = 2.dp,
                color = Color(0xFF42EFC4),
                shape = RoundedCornerShape(20.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddGroupScreen(viewModel: GroupsViewModel, onBack: () -> Unit) {

    var groupName by remember { mutableStateOf("") }
    var memberName by remember { mutableStateOf("") }
    val members = remember { mutableStateListOf<Person>() }
    var description by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("EUR") }
    val currencies = listOf("USD", "EUR", "GBP", "JPY", "AUD", "DKK")



    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "New Group",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF42EFC4))
                .padding(16.dp)

        )
        Text(
            text = "Add Group",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 26.dp)
        )

        CustomTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = "Group Name"
        )
        Text(
            text = "Description",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp)
        )
        CustomTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description",
        )
        Text(
            text = "Choose currency",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp)
        )
        CurrencySelector(
            selectedCurrency = selectedCurrency,
            onCurrencySelected = { selectedCurrency = it },
            currencies = currencies
        )
        Text(
            text = "Members",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .border(
                        width = 2.dp,
                        color = Color(0xFF42EFC4),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (memberName.isEmpty()) {
                    Text(
                        text = "Member Name",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))


            Button(
                onClick = {
                    if (memberName.isNotBlank()) {
                        members.add(Person(memberName))
                        memberName = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF42EFC4),
                    contentColor = Color.Black
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Add")
            }

        }
        Column(modifier = Modifier.padding(start = 26.dp)) {
            members.forEach { member ->
                Text(
                    text = "• ${member.name}",
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
        Button(
            onClick = {
                if (groupName.isNotBlank()) {
                    viewModel.addGroup(groupName, description, selectedCurrency, members)
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

        Button(
            onClick = { onBack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF42EFC4),
                contentColor = Color.Black
            ),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Cancel")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddGroupScreenPreview() {

    val fakeViewModel: GroupsViewModel = viewModel()

    AddGroupScreen(
        viewModel = fakeViewModel,
        onBack = {}
    )
}

