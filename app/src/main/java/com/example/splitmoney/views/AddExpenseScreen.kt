package com.example.splitmoney.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitmoney.viewModels.GroupViewModel
import com.example.splitmoney.viewModels.UserViewModel
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.model.Group
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.ui.theme.SplitMoneyTheme

@Composable
fun AddExpenseScreen(
    groupId: Int,
    loggedInUserId: Int,
    expenseViewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Text(
                "New Expense",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            CustomTextField(value = description, onValueChange = { description = it }, label = "Description")
            Spacer(Modifier.height(16.dp))

            CustomTextField(value = amount, onValueChange = { amount= it }, label = "Amount")
            Spacer(Modifier.height(16.dp))

            errorMsg?.let { Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)) }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (description.isBlank()) {
                        errorMsg = "Description cannot be empty"
                        return@Button
                    }

                    val amt = amount.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMsg = "Enter correct amount"
                        return@Button
                    }

                    val expense = Expense(
                        group_id = groupId,
                        description = description,
                        total_amount = amt,
                        paid_by = loggedInUserId
                    )

                    expenseViewModel.addExpense(expense)

                    description = ""
                    amount = ""
                    errorMsg = null

                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Expense")
            }

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
        }
        }
}


