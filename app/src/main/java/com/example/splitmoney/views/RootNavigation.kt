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
fun RootNavigation(
    groupViewModel: GroupViewModel,
    expenseShareViewModel: ExpenseShareViewModel,
    expenseViewModel: ExpenseViewModel,
    groupUserViewModel: GroupUserViewModel,
    userViewModel: UserViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val loggedUserId by userViewModel.loggedUserId.collectAsState()

    val startDestination = if (loggedUserId != null) "main" else "first"

    LaunchedEffect(loggedUserId) {
        if (loggedUserId == null) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute != "login" && currentRoute != "register" && currentRoute != "first") {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        } else {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute == "login" || currentRoute == "register" || currentRoute == "first") {
                navController.navigate("main") {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }


    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("first") {
            FirstScreen(
                onOpen = {
                    navController.navigate("login") {
                        popUpTo("first") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            val loginError by userViewModel.loginError.collectAsState()

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

        composable("main") {
            if (loggedUserId != null) {
                AppNavigation(
                    groupViewModel,
                    expenseShareViewModel,
                    expenseViewModel,
                    groupUserViewModel,
                    userViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        }
    }
}