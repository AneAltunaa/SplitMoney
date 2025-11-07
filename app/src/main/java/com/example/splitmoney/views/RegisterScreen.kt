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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.splitmoney.MainActivity

//Change everything so it is a register page/not login
class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen(onLogin = { email, password ->//pass parameters when we have them
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            })
        }
    }
}
@Composable
fun RegisterScreen(onLogin: (email: String, password: String) -> Unit){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF42EFC4))
    ){
        Box(modifier = Modifier
            .background(Color(0xFFFFFFFF))){
        Column(
            modifier = Modifier.fillMaxSize().padding(all=16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(text = "SplitMo", style= MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(height = 16.dp))
            TextField(value=name, onValueChange = { name = it }, label={Text(text = "Name")})
            Spacer(modifier = Modifier.height(height = 16.dp))
            TextField(value=surname, onValueChange = { surname = it }, label={Text(text = "Surname")})
            Spacer(modifier = Modifier.height(height = 16.dp))
            TextField(value=email, onValueChange = { email = it }, label={Text(text = "Email")})
            Spacer(modifier = Modifier.height(height = 16.dp))
            TextField(value=phoneNumber, onValueChange = { phoneNumber = it }, label={Text(text = "Phone Number")})
            Spacer(modifier = Modifier.height(height = 16.dp))
            TextField(value=password, onValueChange = {password = it}, label={Text(text = "Password")}, visualTransformation = PasswordVisualTransformation())
            Button(onClick = {onLogin(email, password)}) { Text("Register")}

        }
    }
    }

}