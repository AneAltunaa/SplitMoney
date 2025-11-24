package com.example.splitmoney.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.data.model.ExpenseShare
import com.example.splitmoney.data.model.User
import com.example.splitmoney.viewModels.ExpenseShareViewModel
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.viewModels.GroupViewModel

@Composable
fun MemberCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Text(name, fontSize = 16.sp)
        }
    }
}

@Composable
fun ExpandableExpenseCard(
    expense: Expense,
    groupId: Int,
    shareViewModel: ExpenseShareViewModel,
    expenseViewModel: ExpenseViewModel,
    participants: List<User>,
    colors: ColorScheme,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    loggedInUserId: Int
) {
    var expanded by remember { mutableStateOf(false) }
    var sharesForThisExpense by remember { mutableStateOf<List<ExpenseShare>>(emptyList()) }

    LaunchedEffect(expense.id) {
        sharesForThisExpense = shareViewModel.repo.getSharesByExpense(expense.id!!) // acceso directo al repo o ViewModel función suspend
    }

    val payerName = participants.find { it.id == expense.paid_by }?.let { "${it.name} ${it.lastname}" } ?: "Unknown"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(expense.description, fontWeight = FontWeight.Bold)
                    Text(
                        "Paid by: $payerName",
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${expense.total_amount}€",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowRight,
                        contentDescription = "expand",
                        tint = colors.onSurface
                    )
                }
            }

            AnimatedVisibility(expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    sharesForThisExpense.forEach { share ->
                        ExpenseShareItem(
                            share = share,
                            participants = participants,
                            loggedInUserId = loggedInUserId,
                            onMarkPaid = {
                                // backend update
                                shareViewModel.updateShare(
                                    share.id!!,
                                    share.copy(paid = 1)
                                )
                                // local UI update
                                sharesForThisExpense = sharesForThisExpense.map {
                                    if (it.id == share.id) it.copy(paid = 1) else it
                                }
                            },
                            shareViewModel = shareViewModel
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Button(
                        onClick = { expenseViewModel.deleteExpense(groupId,expense.id!!)},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}


@Composable
fun ExpenseShareItem(
    share: ExpenseShare,
    participants: List<User>,
    loggedInUserId: Int,
    onMarkPaid: () -> Unit,
    shareViewModel: ExpenseShareViewModel
) {
    val colors = MaterialTheme.colorScheme
    val paidColor =
        if (share.paid == 1)
            colors.primary.copy(alpha = 0.15f)
        else
            colors.secondary.copy(alpha = 0.12f)

    val userName = participants.find { it.id == share.user_id }?.let { "${it.name} ${it.lastname}" } ?: "Unknown"

    Card(
        colors = CardDefaults.cardColors(containerColor = paidColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(userName, fontWeight = FontWeight.Bold)
                Text("Owes: ${share.amount_owed}€")
            }

            when {
                // 1. すでに支払い済みの場合: 何も表示しないか、「Paid」と表示する
                share.paid == 1 -> {
                    Text(
                        text = "Paid",
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
                // 2. 自分の未払いの場合: 「I paid」ボタンを表示
                share.user_id == loggedInUserId -> {
                    Button(onClick = onMarkPaid) {
                        Text("I paid")
                    }
                }
                // 3. 他人の未払いの場合: 「催促（ベル）」ボタンを表示
                else -> {
                    IconButton(onClick = {
                        shareViewModel.sendReminder(share.id!!)
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = "notify")
                    }
                }
            }
        }
    }
}

@Composable
fun GroupDetailScreen(
    groupId: Int,
    groupViewModel: GroupViewModel,
    groupUserViewModel: GroupUserViewModel,
    expenseViewModel: ExpenseViewModel,
    shareViewModel: ExpenseShareViewModel,
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    loggedInUserId: Int
) {
    val colors = MaterialTheme.colorScheme
    val group by groupViewModel.selectedGroup.collectAsState()
    val participants by groupUserViewModel.participants.collectAsState()
    val expenses by expenseViewModel.expenses.collectAsState()

    LaunchedEffect(groupId) {
        groupViewModel.loadGroupById(groupId)
        groupUserViewModel.loadParticipants(groupId)
        expenseViewModel.loadExpensesByGroup(groupId)
    }

    if (group == null) {
        Text("Loading...", modifier = Modifier.padding(16.dp))
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            AppTopBar(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // -------------------------------
                // 1) GROUP INFO + MEMBERS
                // -------------------------------
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                group!!.name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurface
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                group!!.description,
                                fontSize = 16.sp,
                                color = colors.onSurfaceVariant
                            )

                            Spacer(Modifier.height(16.dp))

                            // 🔹 View Balances button ΠΑΝΩ ΠΑΝΩ (στο group card)
                            Button(
                                onClick = { navController.navigate("balances/$groupId") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Balances")
                            }

                            Spacer(Modifier.height(16.dp))

                            // Members
                            Text("Members", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))

                            participants.forEach { user ->
                                MemberCard("${user.name} ${user.lastname}")
                                Spacer(Modifier.height(8.dp))
                            }

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = { navController.navigate("editGroup/$groupId") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Edit Group")
                            }
                        }
                    }
                }

                // -------------------------------
                // EXPENSES
                // -------------------------------
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                "Expenses",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            // 🔹 Create Expense ΚΑΤΩ ΑΠΟ ΤΟΝ TITLE "Expenses"
                            Button(
                                onClick = { navController.navigate("addExpense/$groupId") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Create Expense")
                            }

                            Spacer(Modifier.height(12.dp))

                            expenses.forEach { expense ->
                                ExpandableExpenseCard(
                                    expense = expense,
                                    groupId = groupId,
                                    shareViewModel = shareViewModel,
                                    expenseViewModel = expenseViewModel,
                                    participants = participants,
                                    colors = colors,
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = onToggleTheme,
                                    loggedInUserId = loggedInUserId
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }

        BottomBar(
            navController = navController,
            currentScreen = "groupDetail",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}



