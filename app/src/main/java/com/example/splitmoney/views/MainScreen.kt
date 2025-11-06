package com.example.splitmoney.views

import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.Card
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.splitmoney.data.SplitMoney
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.ArrowForwardIos
import com.example.splitmoney.viewModels.GroupsViewModel


@Composable
fun MainScreen(
    viewModel: GroupsViewModel,
    onAddGroupClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()){
    Column(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        Text(
            text = "SplitMO",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF42EFC4))
                .padding(16.dp)

        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.groups) { item ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding( top = 20.dp, start = 20.dp, end = 20.dp)
                    .height(80.dp)
                    .border(2.dp, Color(0xFF42EFC4), shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.name} (${item.currency})",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowForwardIos,
                            contentDescription = "Arrow",
                            tint = Color.Black,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF42EFC4))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Icon(imageVector = Icons.Filled.Home, contentDescription = "Home", tint = Color.Black)

            Icon(
                    imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Account",
            tint = Color.Black,
            )
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = Color.Black
            )
        }
    }
        IconButton(
            onClick =  onAddGroupClick ,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 66.dp)
                .size(50.dp)
                .background(Color(0xFF42EFC4), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    val sampleData = listOf(
//        SplitMoney(id="1", name="Dinner", members = mutableListOf("Alice", "Bob")),
//        SplitMoney(id="1", name="Lunch", members = mutableListOf("Alice", "Bob"))
//    )
//    MainScreen(items = sampleData, onItemClick ={} )
//}