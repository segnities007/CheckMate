package com.segnities007.templates.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.model.WeekDay // Import WeekDay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWeeklyTemplateBottomSheet(
    onDismiss: () -> Unit,
    onCreateTemplate: (name: String, dayOfWeek: WeekDay) -> Unit, // Changed category to dayOfWeek
    sheetState: SheetState,
) {
    var templateName by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }
    var selectedDayOfWeek by remember { mutableStateOf<WeekDay?>(null) } // Changed state variable

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                "テンプレートを作成",
                style = MaterialTheme.typography.headlineSmall,
            )

            // Template Name
            OutlinedTextField(
                value = templateName,
                onValueChange = { templateName = it },
                label = { Text("テンプレート名*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Day of Week Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = !expandedDropdown },
            ) {
                OutlinedTextField(
                    value = selectedDayOfWeek?.name ?: "", // Displaying enum name for now
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("曜日*") }, // Changed label
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                    placeholder = { Text("曜日を選択") }, // Changed placeholder
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false },
                ) {
                    // Filter out UNSPECIFIED if you don't want it to be selectable
                    WeekDay.entries.filter { it != WeekDay.UNSPECIFIED }.forEach { day ->
                        DropdownMenuItem(
                            text = { Text(day.name) }, // Displaying enum name (e.g., MONDAY)
                            onClick = {
                                selectedDayOfWeek = day
                                expandedDropdown = false
                            },
                        )
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) { Text("キャンセル") }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = {
                        if (templateName.isNotBlank() && selectedDayOfWeek != null) {
                            onCreateTemplate(templateName, selectedDayOfWeek!!)
                            onDismiss()
                        }
                    },
                    enabled = templateName.isNotBlank() && selectedDayOfWeek != null
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
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        CreateWeeklyTemplateBottomSheet(
            sheetState = sheetState,
            onDismiss = {},
            onCreateTemplate = { _, _ -> }, // Adjusted for new signature
        )
    }
}
