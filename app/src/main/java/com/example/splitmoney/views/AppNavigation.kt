package com.example.splitmoney.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitmoney.viewModels.*

@Composable
fun AppNavigation(
    groupViewModel: GroupViewModel,
    expenseShareViewModel: ExpenseShareViewModel,
    expenseViewModel: ExpenseViewModel,
    groupUserViewModel: GroupUserViewModel,
    userViewModel: UserViewModel,
    balanceViewModel: BalanceViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val userId by userViewModel.loggedUserId.collectAsState()
    if (userId == null) return
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {

        composable("main") {
            MainScreen(
                viewModel = groupViewModel,
                navController = navController,
                onAddGroupClick = { navController.navigate("addGroup") },
                userId = userId!!,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        composable("profile") {
            UserProfileScreen(
                userViewModel = userViewModel,
                navController = navController,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        composable("updateUser"){
            UpdateUserScreen(userViewModel = userViewModel, onBack = { navController.navigate("profile") })
        }

        composable("settings") {
            // SettingsScreen(navController)
        }

        composable("addGroup") {
            AddGroupScreen(
                groupViewModel = groupViewModel,
                userViewModel = userViewModel,
                loggedInUserId = userId!!,
                onBack = { navController.popBackStack() },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        composable("groupDetail/{groupId}") { backStack ->
            val groupId = backStack.arguments?.getString("groupId")?.toInt()
            if (groupId != null) {
                GroupDetailScreen(
                    groupId = groupId,
                    groupViewModel = groupViewModel,
                    groupUserViewModel = groupUserViewModel,
                    expenseViewModel = expenseViewModel,
                    shareViewModel = expenseShareViewModel,
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    loggedInUserId = userId!!
                )
            }
        }

        composable("addExpense/{groupId}") { backStack ->
            val groupId = backStack.arguments?.getString("groupId")?.toInt()
            if (groupId != null) {
                AddExpenseScreen(
                    groupId = groupId,
                    loggedInUserId = userId!!,
                    expenseViewModel = expenseViewModel,
                    groupUserViewModel = groupUserViewModel,
                    onBack = { navController.popBackStack() },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        }
        composable("balances/{groupId}") { backStack ->
            val groupId = backStack.arguments?.getString("groupId")?.toInt()
            if (groupId != null) {
                BalancesScreen(
                    groupId = groupId,
                    balanceViewModel = balanceViewModel,
                    navController = navController,
                    loggedInUserId = userId!!,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        }

    }
}