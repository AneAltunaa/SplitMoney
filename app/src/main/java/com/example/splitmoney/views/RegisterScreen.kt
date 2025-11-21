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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    onRegister: (name: String, surname: String, email: String, phone: String, password: String) -> Unit,
    onBackToLogin: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val isFormValid = name.isNotBlank() &&
            surname.isNotBlank() &&
            email.isNotBlank() &&
            phoneNumber.isNotBlank() &&
            password.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primary)
    ) {
        IconButton(
            onClick = onBackToLogin,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Previous page - login",
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
                    .padding(all = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(value = name, onValueChange = { name = it }, label = "Name", visualTransformation = VisualTransformation.None)
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(value = surname, onValueChange = { surname = it }, label = "Surname", visualTransformation = VisualTransformation.None)
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(value = email, onValueChange = { email = it }, label = "Email", visualTransformation = VisualTransformation.None)
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = "Phone Number", visualTransformation = VisualTransformation.None)
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(value = password, onValueChange = { password = it }, label = "Password", visualTransformation = PasswordVisualTransformation() )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onRegister(name, surname, email, phoneNumber, password) },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                        disabledContainerColor = colors.primary.copy(alpha = 0.4f),
                        disabledContentColor = colors.onPrimary.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Register")
                }
            }
        }
    }
}
