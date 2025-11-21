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
import androidx.compose.ui.unit.dp
import com.example.splitmoney.data.model.User
import com.example.splitmoney.viewModels.UserViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.VisualTransformation


@Composable
fun UpdateUserScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit

) {

    val snackBar = remember { SnackbarHostState()}
    val scope = rememberCoroutineScope()

    val colors = MaterialTheme.colorScheme

    val currentUser by userViewModel.currentUser.collectAsState()
    val loggedUserId by userViewModel.loggedUserId.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name= user.name
            surname = user.lastname
            email = user.mail
            phoneNumber = user.phone
            password = user.password
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBar) }
    ){paddingValues -> Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.primary)
    ) {
        IconButton(
            onClick = { onBack() } //TODO: Go back to user profile

        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Previous page",
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
                    text = "Update information",
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
                CustomTextField(value = password, onValueChange = { password = it }, label = "Password", visualTransformation = PasswordVisualTransformation())

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val updatedUser = User(
                            id = currentUser!!.id,
                            name = name,
                            lastname = surname,
                            mail = email,
                            phone = phoneNumber,
                            password = password
                        )

                        userViewModel.updateUser(currentUser?.id ?: 0, updatedUser)

                        scope.launch {
                            snackBar.showSnackbar("User updated successfully!")
                        } // a message for whne it is updated
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                        disabledContainerColor = colors.primary.copy(alpha = 0.4f),
                        disabledContentColor = colors.onPrimary.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)

                ) {
                    Text("Update")
                }

            }}
            }

        }
    }
}


