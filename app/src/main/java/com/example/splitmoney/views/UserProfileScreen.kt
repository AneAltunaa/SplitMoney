package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splitmoney.viewModels.UserViewModel

@Composable
fun UserProfileScreen(userViewModel: UserViewModel, navController: NavController) {
    val colorScheme = MaterialTheme.colorScheme
    val currentUser by userViewModel.currentUser.collectAsState()
    val loggedUserId by userViewModel.loggedUserId.collectAsState()

    LaunchedEffect(loggedUserId) {
        loggedUserId?.let { userViewModel.loadUserById(it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 🔹 Top bar
            AppTopBar()

            Box(modifier = Modifier.border(0.5.dp, colorScheme.primary).weight(1f)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.size(200.dp)
                    )

                    if (currentUser == null) {
                        Text("Loading user...", color = colorScheme.onBackground)
                    } else {
                        Text("Name: ${currentUser!!.name}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 26.dp)
                        )
                        Text("Last name: ${currentUser!!.lastname}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 26.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Email: ${currentUser!!.mail}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 26.dp)
                        )
                        Text("Phone: ${currentUser!!.phone}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            fontStyle = FontStyle.Italic,
                            color = colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 26.dp)
                        )
                        Spacer(modifier = Modifier.height(50.dp))
                    }

                    Button(
                        onClick = { navController.navigate("updateUser") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Update User")
                    }
                }
            }
        }

        BottomBar(navController = navController, currentScreen = "profile", modifier = Modifier.align(Alignment.BottomCenter))
    }
}
