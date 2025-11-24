package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.data.model.ExpenseShare
import com.example.splitmoney.data.model.GroupUser
import com.example.splitmoney.data.model.User
import com.example.splitmoney.viewModels.ExpenseShareViewModel
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.viewModels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun EditGroupScreen(
    groupId: Int,
    groupUserViewModel: GroupUserViewModel,
    expenseViewModel: ExpenseViewModel,
    shareViewModel: ExpenseShareViewModel,
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    // συμμετέχοντες + expenses από τα viewModels
    val participants by groupUserViewModel.participants.collectAsState()
    val expenses by expenseViewModel.expenses.collectAsState()

    // shares ανά expense (expenseId -> λίστα shares)
    var sharesByExpense by remember { mutableStateOf<Map<Int, List<ExpenseShare>>>(emptyMap()) }

    // για προσθήκη με email
    var emailToAdd by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val foundUser by userViewModel.foundUser.collectAsState()

    val scope = rememberCoroutineScope()

    // Φόρτωμα δεδομένων
    LaunchedEffect(groupId) {
        groupUserViewModel.loadParticipants(groupId)
        expenseViewModel.loadExpensesByGroup(groupId)
    }

    // Κάθε φορά που αλλάζουν τα expenses, φέρνουμε shares από το repo
    LaunchedEffect(expenses) {
        val newMap = mutableMapOf<Int, List<ExpenseShare>>()
        for (expense in expenses) {
            expense.id?.let { eid ->
                val shares = shareViewModel.repo.getSharesByExpense(eid)
                newMap[eid] = shares
            }
        }
        sharesByExpense = newMap
    }

    Scaffold(
        topBar = {
            AppTopBar(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .padding(16.dp)
        ) {


            Spacer(Modifier.height(8.dp))

            Text(
                text = "Edit Group Members",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.onBackground
            )

            Spacer(Modifier.height(16.dp))

            // -------------------------------
            // Προσθήκη μέλους με email
            // -------------------------------
            Text(
                text = "Add members by email:",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomTextField(
                    value = emailToAdd,
                    onValueChange = { emailToAdd = it },
                    label = "Enter user email",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(colors.primary, shape = RoundedCornerShape(15.dp))
                        .clickable {
                            if (emailToAdd.isBlank()) {
                                errorMsg = "Email cannot be empty"
                            } else {
                                errorMsg = null
                                userViewModel.findUserByEmail(emailToAdd.trim())
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colors.onPrimary
                    )
                }
            }

            foundUser?.let { user ->
                if (!participants.any { it.id == user.id }) {
                    Spacer(Modifier.height(8.dp))
                    UserCard(
                        user = user,
                        onAdd = {
                            scope.launch {
                                // προσθήκη στο group
                                val gu = GroupUser(
                                    group_id = groupId,
                                    user_id = user.id!!
                                )
                                groupUserViewModel.addParticipant(gu)
                                groupUserViewModel.loadParticipants(groupId)
                            }
                            emailToAdd = ""
                            userViewModel.clearFoundUser()
                            errorMsg = null
                        }
                    )
                } else {
                    Text(
                        "User already in group.",
                        color = colors.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Members:",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp),
                color = colors.onBackground
            )

            Spacer(Modifier.height(8.dp))

            // -------------------------------
            // Λίστα μελών με δυνατότητα διαγραφής
            // -------------------------------
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(participants) { user ->
                    val canDelete = canRemoveUserFromGroup(
                        user = user,
                        expenses = expenses,
                        sharesByExpense = sharesByExpense
                    )

                    UserCard(
                        user = user,
                        onRemove = if (canDelete) {
                            {
                                scope.launch {
                                    val gu = GroupUser(
                                        group_id = groupId,
                                        user_id = user.id!!
                                    )
                                    groupUserViewModel.removeParticipant(gu)
                                    groupUserViewModel.loadParticipants(groupId)
                                }
                            }
                        } else null
                    )

                    if (!canDelete) {
                        Text(
                            text = "User cannot be removed (has open balances).",
                            color = colors.onSurface.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            errorMsg?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onBack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ίδια λογική με πριν
fun canRemoveUserFromGroup(
    user: User,
    expenses: List<Expense>,
    sharesByExpense: Map<Int, List<ExpenseShare>>
): Boolean {
    val allShares = sharesByExpense.values.flatten()

    val hasOpenDebts = allShares.any { share ->
        share.user_id == user.id && share.paid == 0
    }

    val hasOthersOwingToHim = allShares.any { share ->
        share.paid == 0 && run {
            val exp = expenses.find { it.id == share.expense_id }
            exp?.paid_by == user.id
        }
    }

    return !hasOpenDebts && !hasOthersOwingToHim
}
