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
import com.example.splitmoney.ui.theme.SplitMoneyTheme
import com.example.splitmoney.viewModels.GroupViewModel

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
fun AddGroupScreen(viewModel: GroupViewModel, onBack: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    var groupName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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

        Button(
            onClick = {
                if (groupName.isNotBlank()) {
                    // TODO: Add addGroup functionality
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
