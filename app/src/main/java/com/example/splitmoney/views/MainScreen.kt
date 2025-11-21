package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.splitmoney.viewModels.GroupViewModel
import android.Manifest
import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: GroupViewModel,
    navController: NavController,
    onAddGroupClick: () -> Unit,
    userId: Int,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val groups by viewModel.groups.collectAsState()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

        LaunchedEffect(key1 = true) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }

    LaunchedEffect(userId) { viewModel.loadGroupsByUser(userId) }

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            AppTopBar(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)

            // Groups List
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(groups) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                            .height(80.dp)
                            .border(2.dp, colors.primary, shape = RoundedCornerShape(16.dp))
                            .clickable { navController.navigate("groupDetail/${item.id}") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = item.name,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = colors.onSurface,
                                modifier = Modifier.fillMaxWidth().padding(12.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = "Arrow",
                                tint = colors.onSurface,
                                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Add Button
        IconButton(
            onClick = onAddGroupClick,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp)
                .size(50.dp)
                .background(colors.primary, shape = CircleShape)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add", tint = colors.onPrimary)
        }

        // BottomBar
        BottomBar(navController = navController, currentScreen = "home", modifier = Modifier.align(Alignment.BottomCenter))
    }
}