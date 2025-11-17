package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTopBar() {
    val colors = MaterialTheme.colorScheme

    Text(
        text = "SplitMO",
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        fontFamily = FontFamily.Serif,
        color = colors.onPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.primary)
            .padding(20.dp)
    )
}