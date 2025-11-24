package com.example.splitmoney.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitmoney.data.model.ExpenseRequest
import com.example.splitmoney.data.model.ExpenseShareRequest
import com.example.splitmoney.viewModels.ExpenseViewModel
import com.example.splitmoney.viewModels.GroupUserViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class SplitType {
    EQUAL, CUSTOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    groupId: Int,
    loggedInUserId: Int,
    expenseViewModel: ExpenseViewModel,
    groupUserViewModel: GroupUserViewModel,
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Συμμετέχοντες στο group
    val participants by groupUserViewModel.participants.collectAsState()

    LaunchedEffect(groupId) {
        groupUserViewModel.loadParticipants(groupId)
    }

    // State φόρμας
    var description by remember { mutableStateOf("") }
    var totalAmountText by remember { mutableStateOf("") }
    var selectedPayerId by remember { mutableStateOf<Int?>(null) }
    var splitType by remember { mutableStateOf(SplitType.EQUAL) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    var selectedUserIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var customAmounts by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    // Default payer + default selected participants
    LaunchedEffect(participants) {
        if (selectedPayerId == null && participants.isNotEmpty()) {
            selectedPayerId = participants.find { it.id == loggedInUserId }?.id
                ?: participants.first().id
        }
        if (selectedUserIds.isEmpty() && participants.isNotEmpty()) {
            selectedUserIds = participants.mapNotNull { it.id }.toSet()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {

            Text(
                text = "New Expense",
                fontSize = 24.sp,
                color = colors.onBackground
            )

            Spacer(Modifier.height(16.dp))

            // Περιγραφή
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = totalAmountText,
                onValueChange = { totalAmountText = it },
                label = { Text("Total amount (€)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))


            Text("Who paid?", color = colors.onBackground)
            Spacer(Modifier.height(4.dp))

            var payerDropdownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = payerDropdownExpanded,
                onExpandedChange = { payerDropdownExpanded = !payerDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = participants.find { it.id == selectedPayerId }?.let {
                        "${it.name} ${it.lastname}"
                    } ?: "Select payer",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payer") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = payerDropdownExpanded,
                    onDismissRequest = { payerDropdownExpanded = false }
                ) {
                    participants.forEach { user ->
                        DropdownMenuItem(
                            text = { Text("${user.name} ${user.lastname}") },
                            onClick = {
                                selectedPayerId = user.id
                                payerDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))


            Text("Split type", color = colors.onBackground)
            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = splitType == SplitType.EQUAL,
                    onClick = { splitType = SplitType.EQUAL },
                    label = { Text("Split equally") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary,
                        selectedLabelColor = colors.onPrimary,
                        containerColor = colors.surface,
                        labelColor = colors.onSurface
                    ),
                    shape = RoundedCornerShape(20.dp)
                    
                )
                FilterChip(
                    selected = splitType == SplitType.CUSTOM,
                    onClick = { splitType = SplitType.CUSTOM },
                    label = { Text("Custom amounts") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = colors.primary,
                        selectedLabelColor = colors.onPrimary,
                        containerColor = colors.surface,
                        labelColor = colors.onSurface,
                    ),
                    shape = RoundedCornerShape(20.dp)

                )
            }

            Spacer(Modifier.height(16.dp))

            // Συμμετέχοντες
            Text("Participants", color = colors.onBackground)
            Spacer(Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                participants.forEach { user ->
                    val isSelected = selectedUserIds.contains(user.id)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected)
                                    colors.primary.copy(alpha = 0.1f)
                                else
                                    colors.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                user.id?.let { uid ->
                                    selectedUserIds =
                                        if (isSelected) selectedUserIds - uid
                                        else selectedUserIds + uid
                                }
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "${user.name} ${user.lastname}",
                                color = colors.onSurface
                            )
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    user.id?.let { uid ->
                                        selectedUserIds =
                                            if (isSelected) selectedUserIds - uid
                                            else selectedUserIds + uid
                                    }
                                }
                            )
                        }

                        if (splitType == SplitType.CUSTOM && isSelected && user.id != null) {
                            Spacer(Modifier.height(4.dp))
                            OutlinedTextField(
                                value = customAmounts[user.id] ?: "",
                                onValueChange = { newValue ->
                                    customAmounts = customAmounts.toMutableMap().apply {
                                        this[user.id] = newValue
                                    }
                                },
                                label = { Text("Amount for this user (€)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Μηνύματα
            successMsg?.let {
                Text(
                    it,
                    color = colors.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            errorMsg?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Save
            Button(
                onClick = {
                    errorMsg = null
                    successMsg = null

                    val total = totalAmountText.toDoubleOrNull()
                    if (description.isBlank()) {
                        errorMsg = "Description cannot be empty"
                        return@Button
                    }
                    if (total == null || total <= 0.0) {
                        errorMsg = "Enter a valid amount"
                        return@Button
                    }
                    if (selectedPayerId == null) {
                        errorMsg = "Select who paid"
                        return@Button
                    }
                    if (selectedUserIds.isEmpty()) {
                        errorMsg = "Select at least one participant"
                        return@Button
                    }

                    val shares: Map<Int, Double> = when (splitType) {
                        SplitType.EQUAL -> {
                            val perPerson = total / selectedUserIds.size
                            selectedUserIds.associateWith { perPerson }
                        }

                        SplitType.CUSTOM -> {
                            val parsed = selectedUserIds.associateWith { uid ->
                                customAmounts[uid]?.toDoubleOrNull() ?: -1.0
                            }

                            if (parsed.values.any { it < 0.0 }) {
                                errorMsg = "Enter valid amounts for all selected users"
                                return@Button
                            }

                            val sum = parsed.values.sum()
                            if (abs(sum - total) > 0.01) {
                                errorMsg =
                                    "Custom amounts must sum up to total (${String.format("%.2f", total)})"
                                return@Button
                            }

                            parsed
                        }
                    }

                    val request = ExpenseRequest(
                        group_id = groupId,
                        description = description,
                        total_amount = total,
                        paid_by = selectedPayerId!!,
                        shares = shares.map { (userId, amount) ->
                            ExpenseShareRequest(
                                user_id = userId,
                                amount_owed = amount
                            )
                        }
                    )

                    scope.launch {
                        try {
                            expenseViewModel.addExpense(request)
                            successMsg = "Expense saved successfully"
                            errorMsg = null
                            onBack()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            successMsg = null
                            errorMsg = "Failed to save expense: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                )
            ) {
                Text("Save Expense")
            }

            Spacer(Modifier.height(8.dp))

            // Cancel
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Cancel")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
