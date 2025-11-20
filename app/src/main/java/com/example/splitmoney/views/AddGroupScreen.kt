package com.example.splitmoney.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitmoney.ui.theme.SplitMoneyTheme
import com.example.splitmoney.viewModels.GroupViewModel
import com.example.splitmoney.viewModels.UserViewModel
import com.example.splitmoney.data.model.User
import com.example.splitmoney.data.model.Group

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .height(56.dp)
            .background(colors.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = label,
                color = colors.onPrimary.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = colors.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart)
        )
    }
}

@Composable
fun UserCard(
    user: User,
    currentUserId: Int? = null,
    onAdd: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (user.id == currentUserId) "${user.name} (You)" else "${user.name} ${user.lastname}",
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            onAdd?.let {
                IconButton(onClick = it) { Text("+", fontSize = 20.sp) }
            }
            if (user.id != currentUserId) {
                onRemove?.let {
                    IconButton(onClick = it) { Text("✕", fontSize = 20.sp, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
fun AddGroupScreen(
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
    loggedInUserId: Int,
    onBack: () -> Unit,
    // DODANI PARAMETRI ZA TEMO
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var emailToAdd by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val foundUser by userViewModel.foundUser.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    var members by remember { mutableStateOf(mutableListOf<User>()) }

    LaunchedEffect(loggedInUserId) { userViewModel.loadUserById(loggedInUserId) }
    LaunchedEffect(currentUser) {
        currentUser?.let {
            // Dodaj trenutnega uporabnika, če še ni na seznamu
            if (!members.any { m -> m.id == it.id }) {
                members = members.toMutableList().apply { add(it) }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(isDarkTheme = isDarkTheme, onToggleTheme = onToggleTheme)
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(colors.background)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "New Group",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 16.dp),
                    textAlign = TextAlign.Center,
                    color = colors.onBackground

                )
                Spacer(Modifier.height(16.dp))

                CustomTextField(value = groupName, onValueChange = { groupName = it }, label = "Group Name", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                CustomTextField(value = description, onValueChange = { description = it }, label = "Description", modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))
                Text("Add members by email:", fontWeight = FontWeight.SemiBold, color = colors.onBackground)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomTextField(
                        value = emailToAdd,
                        onValueChange = { emailToAdd = it },
                        label = "Enter user email",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(15.dp))
                            .clickable {
                                if (emailToAdd.isBlank()) errorMsg = "Email cannot be empty"
                                else userViewModel.findUserByEmail(emailToAdd.trim())
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = colors.onPrimary
                        )
                    }
                }

                foundUser?.let { user ->
                    if (!members.any { it.id == user.id }) {
                        UserCard(user = user, onAdd = {
                            members = members.toMutableList().apply { add(user) }
                            emailToAdd = ""
                            userViewModel.clearFoundUser()
                            errorMsg = null
                        })
                    } else {
                        Text("User already added.", color = colors.primary, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                Text("Members:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp), color = colors.onBackground)

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(members) { user ->
                        UserCard(
                            user = user,
                            currentUserId = loggedInUserId,
                            onRemove = {
                                if (user.id != loggedInUserId) {
                                    members = members.toMutableList().apply { remove(user) }
                                }
                            }
                        )
                    }
                }

                errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (groupName.isBlank()) { errorMsg = "Group name cannot be empty"; return@Button }
                        if (members.size < 2) { errorMsg = "Group must have at least 2 members (including you)"; return@Button }

                        val group = Group(
                            name = groupName,
                            description = description,
                            participants = members.mapNotNull { it.id },
                            id = null
                        )

                        groupViewModel.createGroup(group,
                            onSuccess = { onBack() },
                            onError = { msg -> errorMsg = "Error: $msg" }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Group")
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel")
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    )

}


@Preview()
@Composable
fun AddGroupScreenDarkPreview() {
    SplitMoneyTheme(darkTheme = false) {
        AddGroupScreen(
            groupViewModel = viewModel(),
            userViewModel = viewModel(),
            loggedInUserId = 1,
            onBack = {},
            isDarkTheme = false,
            onToggleTheme = {}
        )
    }
}