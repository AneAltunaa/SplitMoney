package com.example.splitmoney.views

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitmoney.viewModels.GroupsViewModel

@Composable
fun AppNavigation(viewModel: GroupsViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {


        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onAddGroupClick = { navController.navigate("addGroup") }
            )
        }


        composable("addGroup") {
            AddGroupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
