package com.example.splitmoney.views

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.splitmoney.viewModels.ExpenseShareViewModel
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.viewModels.GroupViewModel
import com.example.splitmoney.viewModels.UserViewModel

@Composable
fun AppNavigation(groupViewModel: GroupViewModel,
                  expenseShareViewModel: ExpenseShareViewModel,
                  expenseViewModel: ExpenseViewModel,
                  groupUserViewModel: GroupUserViewModel,
                  userViewModel: UserViewModel
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {


        composable("main") {
            MainScreen(
                viewModel = groupViewModel,
                onAddGroupClick = {
                    navController.navigate("addGroup")
                },
                userId = 1
            )
        }


        composable("addGroup") {
            AddGroupScreen(
                groupViewModel = groupViewModel,
                userViewModel = userViewModel,
                loggedInUserId = 1,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
