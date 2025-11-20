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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. Διαχείριση κατάστασης Θέματος
            val systemDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDarkTheme) }
            val onToggleTheme: () -> Unit = { isDarkTheme = !isDarkTheme }

            // 2. Πέρασμα της κατάστασης στο SplitMoneyTheme
            SplitMoneyTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // ViewModels
                    val groupViewModel: GroupViewModel = viewModel()
                    val expenseShareViewModel: ExpenseShareViewModel = viewModel()
                    val expenseViewModel: ExpenseViewModel = viewModel()
                    val groupUserViewModel: GroupUserViewModel = viewModel()
                    val userViewModel: UserViewModel = viewModel()

                    // 3. Κλήση του RootNavigation με τους νέους παραμέτρους
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