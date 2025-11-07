package com.example.splitmoney.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitmoney.MainActivity


class FirstPageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstScreen(onOpen = {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            })
        }
    }
}
@Composable
fun FirstScreen(onOpen: () -> Unit){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF42EFC4))
            .clickable { onOpen()
            }
    ){
    Column(
        modifier = Modifier.fillMaxSize().padding(all=16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(text = "SplitMo", style= MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, fontSize = 45.sp)
        Text(text = "Trip Savings App")
    }}

}