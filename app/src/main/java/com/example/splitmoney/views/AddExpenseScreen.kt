package com.example.splitmoney.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.viewModels.ExpenseViewModel

@Composable
fun AddExpenseScreen(
    groupId: Int,
    loggedInUserId: Int,
    expenseViewModel: ExpenseViewModel = viewModel(),
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppTopBar(
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme
        )

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
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(16.dp))

            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Description") }
            )
            Spacer(Modifier.height(16.dp))

            CustomTextField(
                value = amount,
                onValueChange = {
                    amount = it.filter { char -> char.isDigit() || char == '.' }
                },
                label = "Amount (€)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = "Amount") }
            )
            Spacer(Modifier.height(16.dp))

            errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }

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
                        paid_by = loggedInUserId,
                        id = null
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

            Spacer(Modifier.height(8.dp))

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = modifier
    )
}