package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun AppTopBar(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.primary)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "SplitMO",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            fontFamily = FontFamily.Serif,
            color = colors.onPrimary,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LightMode,
                contentDescription = "Light Mode",
                tint = if (!isDarkTheme) colors.onPrimary else colors.onPrimary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onToggleTheme() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = colors.secondary,
                    uncheckedTrackColor = colors.secondary.copy(alpha = 0.5f),
                    checkedThumbColor = colors.onPrimary,
                    uncheckedThumbColor = colors.onPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.DarkMode,
                contentDescription = "Dark Mode",
                tint = if (isDarkTheme) colors.onPrimary else colors.onPrimary.copy(alpha = 0.5f)
            )
        }
    }
}