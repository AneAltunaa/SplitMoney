package com.example.splitmoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitmoney.ui.theme.SplitMoneyTheme
import com.example.splitmoney.viewModels.ExpenseShareViewModel
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import com.example.splitmoney.viewModels.GroupViewModel
import com.example.splitmoney.viewModels.UserViewModel
import com.example.splitmoney.views.RootNavigation
import com.example.splitmoney.data.repository.UserRepository // Potreben uvoz za Repozitorij
import androidx.lifecycle.ViewModel // Potreben uvoz
import androidx.lifecycle.ViewModelProvider // Potreben uvoz za Factory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDarkTheme) }
            val onToggleTheme: () -> Unit = { isDarkTheme = !isDarkTheme }

            val userRepository = remember { UserRepository() }
            val viewModelFactory = remember { ViewModelFactory(userRepository) }

            SplitMoneyTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val groupViewModel: GroupViewModel = viewModel(factory = viewModelFactory)
                    val expenseShareViewModel: ExpenseShareViewModel = viewModel(factory = viewModelFactory)
                    val expenseViewModel: ExpenseViewModel = viewModel(factory = viewModelFactory)
                    val groupUserViewModel: GroupUserViewModel = viewModel(factory = viewModelFactory)
                    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory)

                    RootNavigation(
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
}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> UserViewModel(userRepository) as T
            modelClass.isAssignableFrom(GroupViewModel::class.java) -> GroupViewModel() as T
            modelClass.isAssignableFrom(ExpenseShareViewModel::class.java) -> ExpenseShareViewModel() as T
            modelClass.isAssignableFrom(ExpenseViewModel::class.java) -> ExpenseViewModel() as T
            modelClass.isAssignableFrom(GroupUserViewModel::class.java) -> GroupUserViewModel() as T
            else -> throw IllegalArgumentException("Neznan ViewModel razred: ${modelClass.name}")
        }
    }
}

class GroupViewModel : ViewModel()
class ExpenseShareViewModel : ViewModel()
class ExpenseViewModel : ViewModel()
class GroupUserViewModel : ViewModel()