package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomBar(navController: NavController, currentScreen: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.primary)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Home, "Home", tint = colors.onPrimary, modifier = Modifier.clickable {
            if (currentScreen != "home") navController.navigate("main") { launchSingleTop = true }
        })

        Icon(Icons.Filled.AccountCircle, "Profile", tint = colors.onPrimary, modifier = Modifier.clickable {
            if (currentScreen != "profile") navController.navigate("profile") { launchSingleTop = true }
        })

    }
}
