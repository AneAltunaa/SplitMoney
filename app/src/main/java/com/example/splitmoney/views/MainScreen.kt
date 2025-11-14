package com.example.splitmoney.views

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitmoney.viewModels.GroupsViewModel
import com.example.splitmoney.ui.theme.SplitMoneyTheme

@Composable
fun MainScreen(
    viewModel: GroupsViewModel,
    onAddGroupClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(colors.background)) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 🔹 Top Bar
            Text(
                text = "SplitMO",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Serif,
                color = colors.onPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .padding(16.dp)
            )

            // 🔹 Groups List
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viewModel.groups) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                            .height(80.dp)
                            .border(2.dp, colors.primary, shape = RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surface)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${item.name} (${item.currency})",
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = colors.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowForwardIos,
                                contentDescription = "Arrow",
                                tint = colors.onSurface,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 12.dp)
                            )
                        }
                    }
                }
            }

            // 🔹 Bottom Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = colors.onPrimary
                )
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Account",
                    tint = colors.onPrimary,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, UserProfileActivity::class.java))
                    }
                )
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = colors.onPrimary
                )
            }
        }

        // 🔹 Floating Add Button
        IconButton(
            onClick = onAddGroupClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 66.dp)
                .size(50.dp)
                .background(colors.primary, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                tint = colors.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


//@Preview(showBackground = true, name = "Light Mode")
//@Composable
//fun MainScreenLightPreview() {
//    SplitMoneyTheme(darkTheme = false) {
//        MainScreen(viewModel = GroupsViewModel(), onAddGroupClick = {})
//    }
//}
//
//@Preview(showBackground = true, name = "Dark Mode")
//@Composable
//fun MainScreenDarkPreview() {
//    SplitMoneyTheme(darkTheme = true) {
//        MainScreen(viewModel = GroupsViewModel(), onAddGroupClick = {})
//    }
//}