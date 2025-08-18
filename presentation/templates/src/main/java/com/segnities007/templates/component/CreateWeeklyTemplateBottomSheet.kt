package com.segnities007.templates.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateWeeklyTemplateBottomSheet(
    onDismiss: () -> Unit,
    onCreateTemplate: (name: String, daysOfWeek: Set<DayOfWeek>) -> Unit, // Changed signature
    sheetState: SheetState,
) {
    var templateName by remember { mutableStateOf("") }
    val selectedDaysOfWeek = remember { mutableStateOf(emptySet<DayOfWeek>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            // Added vertical padding too
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                "テンプレートを作成",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            OutlinedTextField(
                value = templateName,
                onValueChange = { templateName = it },
                label = { Text("テンプレート名*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "曜日*",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                // Use FlowRow to allow chips to wrap to the next line
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp), // Spacing between rows of chips
                ) {
                    DayOfWeek.entries.forEach { day ->
                        val isSelected = selectedDaysOfWeek.value.contains(day)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedDaysOfWeek.value =
                                    if (isSelected) {
                                        selectedDaysOfWeek.value - day
                                    } else {
                                        selectedDaysOfWeek.value + day
                                    }
                            },
                            label = { Text(day.name) }, // Consider using a display name if available
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) { Text("キャンセル") }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        if (templateName.isNotBlank() && selectedDaysOfWeek.value.isNotEmpty()) {
                            onCreateTemplate(templateName, selectedDaysOfWeek.value)
                            onDismiss()
                        }
                    },
                    // Enable button only if name is not blank and at least one day is selected
                    enabled = templateName.isNotBlank() && selectedDaysOfWeek.value.isNotEmpty(),
                ) {
                    Text("作成")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CreateWeeklyTemplateBottomSheetPreview() {
    MaterialTheme {
        val sheetState =
            rememberModalBottomSheetState(skipPartiallyExpanded = false)
        CreateWeeklyTemplateBottomSheet(
            sheetState = sheetState,
            onDismiss = {},
            onCreateTemplate = { _, _ -> }, // Adjusted for new signature
        )
    }
}
