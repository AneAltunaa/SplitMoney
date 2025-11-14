package com.example.splitmoney.views

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitmoney.viewModels.GroupsViewModel

@Composable
fun RootNavigation(groupsViewModel: GroupsViewModel) {
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
            LoginScreen(
                onLogin = { email, password ->
                    // TODO: validation / API call αν θέλεις
                    navController.navigate("main") {
                        popUpTo("first") { inclusive = true }
                    }
                },
                onRegister = {
                    navController.navigate("register")
                },
                onBack = {
                    navController.navigate("first") {
                        popUpTo("first") { inclusive = true }
                    }
                }
            )
        }

        // 3) Register
        composable("register") {
            RegisterScreen(
                onRegister = { email, password ->
                    // TODO: register λογική
                    navController.navigate("main") {
                        popUpTo("first") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 4) Main app (εκεί που έχεις ήδη το navigation σου)
        composable("main") {
            AppNavigation(groupsViewModel)
        }
    }
}
