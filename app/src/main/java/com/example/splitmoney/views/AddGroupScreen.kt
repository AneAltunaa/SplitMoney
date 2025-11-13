package com.example.splitmoney.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitmoney.data.Person
import com.example.splitmoney.viewModels.GroupsViewModel
import com.example.splitmoney.ui.theme.SplitMoneyTheme

@Composable
fun CurrencySelector(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    currencies: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .clickable { expanded = !expanded }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = selectedCurrency,
                    color = colors.onPrimary,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Currency",
                    tint = colors.secondary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency, color = colors.onBackground) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
            .background(colors.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = label,
                color = colors.onPrimary,
                fontSize = 16.sp
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddGroupScreen(viewModel: GroupsViewModel, onBack: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    var groupName by remember { mutableStateOf("") }
    var memberName by remember { mutableStateOf("") }
    val members = remember { mutableStateListOf<Person>() }
    var description by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("EUR") }
    val currencies = listOf("USD", "EUR", "GBP", "JPY", "AUD", "DKK")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colors.background)
    ) {
        Text(
            text = "New Group",
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

        Text(
            text = "Add Group",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            color = colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 26.dp)
        )

        CustomTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = "Group Name"
        )

        Text(
            text = "Description",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            color = colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp)
        )

        CustomTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description"
        )

        Text(
            text = "Choose currency",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            color = colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp)
        )

        CurrencySelector(
            selectedCurrency = selectedCurrency,
            onCurrencySelected = { selectedCurrency = it },
            currencies = currencies
        )

        Text(
            text = "Members",
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Start,
            color = colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .background(colors.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (memberName.isEmpty()) {
                    Text(
                        text = "Member Name",
                        color = colors.onPrimary,
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    singleLine = true,
                    textStyle = TextStyle(color = colors.onBackground, fontSize = 16.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (memberName.isNotBlank()) {
                        members.add(Person(memberName))
                        memberName = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Add")
            }
        }

        Column(modifier = Modifier.padding(start = 26.dp)) {
            members.forEach { member ->
                Text(
                    text = "• ${member.name}",
                    color = colors.onSurface.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        Button(
            onClick = {
                if (groupName.isNotBlank()) {
                    viewModel.addGroup(groupName, description, selectedCurrency, members)
                    onBack()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Add")
        }

        Button(
            onClick = { onBack() },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Cancel")
        }
    }
}



@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun AddGroupScreenDarkPreview() {
    SplitMoneyTheme(darkTheme = true) {
        AddGroupScreen(viewModel = viewModel(), onBack = {})
    }
}
