package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splitmoney.viewModels.BalanceViewModel

@Composable
fun BalancesScreen(
    groupId: Int,
    balanceViewModel: BalanceViewModel,
    navController: NavController,
    loggedInUserId: Int,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val balancesState by balanceViewModel.balances.collectAsState()

    LaunchedEffect(groupId) {
        balanceViewModel.loadBalances(groupId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            AppTopBar(isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Text(
                    "Balances",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = colors.onBackground
                )

                Spacer(Modifier.height(16.dp))

                val balances = balancesState
                if (balances == null) {
                    Text("Loading balances...", color = colors.onBackground)
                } else {

                    // 🔹 1) Βρίσκουμε το δικό σου net balance
                    val myNet = balances.net_balances.find { it.user_id == loggedInUserId }

                    // 🔹 2) Αν χρωστάς (balance < 0), δείχνουμε κουμπί "Settle my balance"
                    if (myNet != null && myNet.balance < 0) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = {
                                balanceViewModel.settleMyDebts(groupId, loggedInUserId)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Settle my balance (${String.format("%.2f", -myNet.balance)} €)")
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    // -------- NET BALANCES (ποιος είναι + / -) --------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Who is up / down",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.onSurface
                            )
                            Spacer(Modifier.height(12.dp))

                            balances.net_balances.forEach { nb ->
                                val text = when {
                                    nb.balance > 0 ->
                                        "${nb.name} should receive ${"%.2f".format(nb.balance)} €"
                                    nb.balance < 0 ->
                                        "${nb.name} should pay ${"%.2f".format(-nb.balance)} €"
                                    else ->
                                        "${nb.name} is settled"
                                }

                                Text(text, color = colors.onSurface)
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // -------- SUGGESTED SETTLEMENTS --------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Suggested payments",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.onSurface
                            )
                            Spacer(Modifier.height(12.dp))

                            if (balances.settlements.isEmpty()) {
                                Text(
                                    "Everybody is settled 🎉",
                                    color = colors.onSurface
                                )
                            } else {
                                balances.settlements.forEach { s ->
                                    val fromName = balances.net_balances
                                        .find { it.user_id == s.from }?.name
                                        ?: "User ${s.from}"

                                    val toName = balances.net_balances
                                        .find { it.user_id == s.to }?.name
                                        ?: "User ${s.to}"

                                    Text(
                                        "$fromName → $toName: ${"%.2f".format(s.amount)} €",
                                        color = colors.onSurface
                                    )
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }

        BottomBar(
            navController = navController,
            currentScreen = "balances",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


