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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.splitmoney.MainActivity


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(onLogin = { email, password ->//pass parameters when we have them
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            onRegister = {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            })

            BackFirstPage(this)

        }
    }
}

@Composable
fun BackFirstPage(activity: Activity) {
    IconButton(
        onClick = {
            val intent = Intent(activity, FirstPageActivity::class.java)
            activity.startActivity(intent)
            activity.finish() },
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Previous page",
            tint = Color.Black
        )
    }
}
@Composable
fun LoginScreen(onLogin: (email: String, password: String) -> Unit, onRegister: () -> Unit){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            Text(text = "Login", style = MaterialTheme.typography.headlineLarge)
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label =  "Email")

            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                //visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = { onLogin(email, password) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF42EFC4),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp)) { Text("Login") }

            Spacer(modifier = Modifier.height(height = 100.dp))
            Text(text = "Don't have an account: ")
            Button(onClick = { onRegister() },
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF42EFC4),
                    contentColor = Color.Black
                     ),
                    shape = RoundedCornerShape(16.dp)) { Text("Register") }
        }
        }
    }

    }