package com.example.splitmoney.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.core.content.ContextCompat.startActivity
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
            BackLogin(this)
        }
    }
}

@Composable
fun BackLogin(activity: Activity) {
    IconButton(
        onClick = {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
            activity.finish() },
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Previous page - login",
            tint = Color.Black
        )
    }
}

@Composable
fun RegisterScreen(onLogin: (email: String, password: String) -> Unit){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var reenterPass by remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF42EFC4))
    ) {
        Text(text = "SnapRec", style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding( 50.dp)
                .background(Color(0xFFFFFFFF), RoundedCornerShape(16.dp))

        ){
            Column(
                modifier = Modifier.fillMaxSize().padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Register", style = MaterialTheme.typography.headlineLarge)

                CustomTextField(value=name, onValueChange = { name = it }, label= "Name")

                CustomTextField(value=surname, onValueChange = { surname = it }, label="Surname")

                CustomTextField(value=email, onValueChange = { email = it }, label="Email")

                CustomTextField(value=phoneNumber, onValueChange = { phoneNumber = it }, label="Phone Number")

                CustomTextField(value=password, onValueChange = {password = it}, label= "Password")//, visualTransformation = PasswordVisualTransformation())
                CustomTextField(value=reenterPass, onValueChange = {password = it}, label="Re-Enter Password")//, visualTransformation = PasswordVisualTransformation())
                Button(onClick = {onLogin(email, password)},
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF42EFC4),
                    contentColor = Color.Black
                ),
                    shape = RoundedCornerShape(16.dp)) { Text("Register")}
            }

        }
    }


}