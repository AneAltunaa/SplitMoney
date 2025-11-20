package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.model.ExpenseRequest
import com.example.splitmoney.data.model.ExpenseShareRequest
import com.example.splitmoney.viewModels.GroupUserViewModel
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
    expenseViewModel: ExpenseViewModel,
    groupUserViewModel: GroupUserViewModel,
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    // Φέρνουμε τους participants του group
    val participants by groupUserViewModel.participants.collectAsState()

    // Ποιοι συμμετέχουν στο συγκεκριμένο expense
    var selectedUserIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Όταν φορτώσουν οι participants, default είναι ΟΛΟΙ επιλεγμένοι
    LaunchedEffect(groupId) {
        groupUserViewModel.loadParticipants(groupId)
    }

    LaunchedEffect(participants) {
        if (participants.isNotEmpty() && selectedUserIds.isEmpty()) {
            selectedUserIds = participants.mapNotNull { it.id }.toSet()
        }
    }

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

            // --- Split between which members ---
            Text(
                "Split between:",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(8.dp))

            if (participants.isEmpty()) {
                Text("No members in this group", color = Color.Gray)
            } else {
                Column {
                    participants.forEach { user ->
                        val uid = user.id ?: return@forEach
                        val checked = selectedUserIds.contains(uid)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedUserIds =
                                        if (checked) selectedUserIds - uid else selectedUserIds + uid
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    selectedUserIds =
                                        if (it) selectedUserIds + uid else selectedUserIds - uid
                                }
                            )
                            Text("${user.name} ${user.lastname}")
                        }
                    }
                }
            }

            errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
            errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }

            Spacer(Modifier.height(24.dp))

            // --- Add Expense button ---
            Button(
                onClick = {
                    // Validation
                    if (description.isBlank()) {
                        errorMsg = "Description cannot be empty"
                        return@Button
                    }

                    val amt = amount.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMsg = "Enter correct amount"
                        return@Button
                    }

                    if (selectedUserIds.isEmpty()) {
                        errorMsg = "Select at least one participant"
                        return@Button
                    }
                    val shareAmount = amt / selectedUserIds.size

                    val shares = selectedUserIds.map { uid ->
                        ExpenseShareRequest(
                            user_id = uid,
                            amount_owed = shareAmount
                        )
                    }


                    val request = ExpenseRequest(
                        group_id = groupId,
                        description = description,
                        total_amount = amt,
                        paid_by = loggedInUserId,
                        shares = shares
                    )

                    isSaving = true
                    errorMsg = null

                    // Στέλνουμε στο ViewModel → Repository → Retrofit → Backend
                    expenseViewModel.addExpense(request)

                    // Εδώ απλά “καθαρίζουμε” και γυρνάμε πίσω
                    description = ""
                    amount = ""
                    isSaving = false
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                Text(if (isSaving) "Saving..." else "Add Expense")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
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