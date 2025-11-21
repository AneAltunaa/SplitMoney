package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// CustomTextField z barvami in zaobljenimi robovi
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = visualTransformation,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit,
    loginError: String?
) {
    val colors = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primary)
    ) {

        // Back arrow
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.onPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp)
                .background(colors.surface, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email plain text
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    visualTransformation = VisualTransformation.None
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Password masked
                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    visualTransformation = PasswordVisualTransformation()
                )

                // Login error
                if (loginError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = loginError,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Login button
                Button(
                    onClick = { onLogin(email, password) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Don't have an account:",
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Register button
                Button(
                    onClick = { onRegister() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register")
                }
            }
        }
    }
}
