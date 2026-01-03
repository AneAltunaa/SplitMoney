package com.example.splitmoney.views


import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splitmoney.data.model.Expense
import com.example.splitmoney.data.model.ExpenseShare
import com.example.splitmoney.data.model.User
import com.example.splitmoney.viewModels.BalanceViewModel
import com.example.splitmoney.viewModels.ExpenseShareViewModel
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.viewModels.GroupViewModel
//
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
    balanceViewModel: BalanceViewModel,
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    loggedInUserId: Int
) {
    val colors = MaterialTheme.colorScheme
    val group by groupViewModel.selectedGroup.collectAsState()
    val participants by groupUserViewModel.participants.collectAsState()
    val expenses by expenseViewModel.expenses.collectAsState()
    val balancesState by balanceViewModel.balances.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Expenses", "Balances", "Group Info")

    val cardColors = CardDefaults.cardColors(
        containerColor = colors.onPrimary,
        contentColor = colors.primary
    )
    val cardElevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

    LaunchedEffect(groupId) {
        groupViewModel.loadGroupById(groupId)
        groupUserViewModel.loadParticipants(groupId)
        expenseViewModel.loadExpensesByGroup(groupId)
        balanceViewModel.loadBalances(groupId)
    }

    if (group == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            AppTopBar(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = colors.surfaceVariant,
                contentColor = colors.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                when (selectedTabIndex) {

                    0 -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            item { Spacer(Modifier.height(8.dp)) }
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = cardColors,
                                    elevation = cardElevation
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Expenses",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(12.dp))
                                        Button(
                                            onClick = { navController.navigate("addExpense/$groupId") },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Create Expense")
                                        }
                                        Spacer(Modifier.height(16.dp))
                                        expenses.forEach { expense ->
                                            ExpandableExpenseCard(
                                                expense,
                                                groupId,
                                                shareViewModel,
                                                expenseViewModel,
                                                participants,
                                                colors,
                                                isDarkTheme,
                                                onToggleTheme,
                                                loggedInUserId
                                            )
                                            Spacer(Modifier.height(12.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }


                    1 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item { Spacer(Modifier.height(8.dp)) }
                            item {
                                val balances = balancesState
                                if (balances == null) {
                                    Text("Loading...", modifier = Modifier.padding(16.dp))
                                } else {
                                    Column {


                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = cardColors,
                                            elevation = cardElevation
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.AccountBalanceWallet,
                                                        contentDescription = null
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        "Group Summary",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp
                                                    )
                                                }
                                                Spacer(Modifier.height(16.dp))
                                                balances.net_balances.forEachIndexed { index, nb ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth()
                                                            .padding(vertical = 8.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(
                                                            nb.name,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        val isPos = nb.balance > 0
                                                        val isNeg = nb.balance < 0
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(
                                                                text = if (isPos) "+${
                                                                    "%.2f".format(
                                                                        nb.balance
                                                                    )
                                                                } €" else if (isNeg) "${
                                                                    "%.2f".format(
                                                                        nb.balance
                                                                    )
                                                                } €" else "Settled",
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (isPos) androidx.compose.ui.graphics.Color(
                                                                    0xFF4CAF50
                                                                ) else if (isNeg) colors.error else colors.primary.copy(
                                                                    0.6f
                                                                )
                                                            )
                                                            Spacer(Modifier.width(4.dp))
                                                            Icon(
                                                                imageVector = if (isPos) Icons.Default.TrendingUp else if (isNeg) Icons.Default.TrendingDown else Icons.Default.CheckCircle,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(18.dp),
                                                                tint = if (isPos) androidx.compose.ui.graphics.Color(
                                                                    0xFF4CAF50
                                                                ) else if (isNeg) colors.error else colors.primary.copy(
                                                                    0.6f
                                                                )
                                                            )
                                                        }
                                                    }
                                                    if (index < balances.net_balances.size - 1) HorizontalDivider(
                                                        thickness = 0.5.dp,
                                                        color = colors.primary.copy(0.1f)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.height(16.dp))


                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = cardColors,
                                            elevation = cardElevation
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.SwapHoriz,
                                                        contentDescription = null,
                                                        tint = colors.primary
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        "Suggested Payments",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp
                                                    )
                                                }

                                                Spacer(Modifier.height(12.dp))

                                                if (balances.settlements.isEmpty()) {
                                                    Text(
                                                        "Everyone is even! 🎉",
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                    )
                                                } else {
                                                    balances.settlements.forEach { s ->
                                                        val fromName =
                                                            balances.net_balances.find { it.user_id == s.from }?.name
                                                                ?: "User"
                                                        val toName =
                                                            balances.net_balances.find { it.user_id == s.to }?.name
                                                                ?: "User"

                                                        Surface(
                                                            color = colors.primary.copy(alpha = 0.05f),
                                                            shape = RoundedCornerShape(12.dp),
                                                            modifier = Modifier.fillMaxWidth()
                                                                .padding(vertical = 6.dp)
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(16.dp)
                                                                    .fillMaxWidth(),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {

                                                                Column(modifier = Modifier.weight(1f)) {
                                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                                        Spacer(Modifier.width(4.dp))
                                                                        Text(
                                                                            text = fromName,
                                                                            fontWeight = FontWeight.Bold,
                                                                            fontSize = 14.sp
                                                                        )
                                                                    }

                                                                    Icon(
                                                                        Icons.Default.ArrowDownward,
                                                                        contentDescription = null,
                                                                        modifier = Modifier.padding(
                                                                            start = 14.dp
                                                                        ).size(14.dp),
                                                                        tint = colors.primary.copy(
                                                                            alpha = 0.5f
                                                                        )
                                                                    )

                                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                                        Spacer(Modifier.width(4.dp))
                                                                        Text(
                                                                            text = toName,
                                                                            fontWeight = FontWeight.Bold,
                                                                            fontSize = 14.sp
                                                                        )
                                                                    }
                                                                }

                                                                Column(horizontalAlignment = Alignment.End) {
                                                                    Text(
                                                                        text = "${"%.2f".format(s.amount)} €",
                                                                        fontWeight = FontWeight.ExtraBold,
                                                                        color = colors.primary,
                                                                        fontSize = 18.sp
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } // 3) TAB: GROUP INFO
                    2 -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            item { Spacer(Modifier.height(8.dp)) }
                            item {
                                Card(modifier = Modifier.fillMaxWidth(), colors = cardColors, elevation = cardElevation) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(group!!.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                        Text(group!!.description)
                                        Spacer(Modifier.height(20.dp))
                                        Text("Members", fontWeight = FontWeight.Bold)
                                        participants.forEach { user -> MemberCard("${user.name} ${user.lastname}"); Spacer(Modifier.height(8.dp)) }
                                        Spacer(Modifier.height(16.dp))
                                        Button(onClick = { navController.navigate("editGroup/$groupId") }, modifier = Modifier.fillMaxWidth()) {
                                            Text("Edit Group")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Back")
            }
            Spacer(Modifier.height(80.dp))
        }
        BottomBar(navController = navController, currentScreen = "groupDetail", modifier = Modifier.align(Alignment.BottomCenter))
    }
}