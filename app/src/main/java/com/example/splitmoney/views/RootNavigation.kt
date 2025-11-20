package com.example.splitmoney.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.splitmoney.viewModels.ExpenseShareViewModel
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.viewModels.GroupViewModel
import com.example.splitmoney.viewModels.UserViewModel

@Composable
fun RootNavigation(groupViewModel: GroupViewModel,
                   expenseShareViewModel: ExpenseShareViewModel,
                   expenseViewModel: ExpenseViewModel,
                   groupUserViewModel: GroupUserViewModel,
                   userViewModel: UserViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "first"
    ) {
        // 1) Πρώτη οθόνη
        composable("first") {
            FirstScreen(
                onOpen = {
                    navController.navigate("login") {
                        popUpTo("first") { inclusive = true }
                    }
                }
            )
        }

        // 2) Login
        composable("login") {
            val loggedUserId by userViewModel.loggedUserId.collectAsState()
            val loginError by userViewModel.loginError.collectAsState()

            LaunchedEffect(loggedUserId) {
                if (loggedUserId != null) {
                    userViewModel.clearLoginError()
                    navController.navigate("main") {
                        popUpTo("first") { inclusive = true }
                    }
                }
            }

            LoginScreen(
                onLogin = { email, password ->
                    userViewModel.clearLoginError()
                    userViewModel.login(email, password)
                },
                onRegister = {
                    navController.navigate("register")
                },
                onBack = {
                    navController.navigate("first") {
                        popUpTo("first") { inclusive = true }
                    }
                },
                loginError = loginError
            )
        }


        // 3) Register
        composable("register") {
            RegisterScreen(
                onRegister = { name, surname, email, phone, password ->
                    userViewModel.register(name, surname, email, phone, password)
                    navController.navigate("login")
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 4) Main app (εκεί που έχεις ήδη το navigation σου)
        composable("main") {
            AppNavigation(groupViewModel,
                expenseShareViewModel,
                expenseViewModel,
                groupUserViewModel,
                userViewModel
            )
        }


    }
}