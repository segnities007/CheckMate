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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.designsystem.theme.Dimens
import com.segnities007.model.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateWeeklyTemplateBottomSheet(
    onDismiss: () -> Unit,
    onCreateTemplate: (name: String, daysOfWeek: Set<DayOfWeek>) -> Unit,
    sheetState: SheetState,
    onImportFromIcs: () -> Unit,
    isImportingIcs: Boolean,
) {
    var templateName by remember { mutableStateOf("") }
    val selectedDaysOfWeek = remember { mutableStateOf(emptySet<DayOfWeek>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // ヘッダー + ICS で作成ボタン
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "テンプレートを作成",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick = onImportFromIcs,
                    enabled = !isImportingIcs,
                ) {
                    if (isImportingIcs) {
                        CircularProgressIndicator(modifier = Modifier.width(Dimens.IconSmall), strokeWidth = 2.dp)
                        Spacer(Modifier.width(Dimens.PaddingSmall))
                        Text("読み込み中")
                    } else {
                        Text("ICSで作成")
                    }
                }
            }

            // テンプレート名入力
            OutlinedTextField(
                value = templateName,
                onValueChange = { templateName = it },
                label = { Text("テンプレート名") },
                placeholder = { Text("例: 月曜日の忘れ物") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
            )

            // 曜日選択
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "適用する曜日",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
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
                            label = {
                                Text(
                                    text = getDayOfWeekDisplayName(day),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                        )
                    }
                }
            }

            // アクションボタン
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                ) {
                    Text("キャンセル")
                }

                Button(
                    onClick = {
                        if (templateName.isNotBlank() && selectedDaysOfWeek.value.isNotEmpty()) {
                            onCreateTemplate(templateName, selectedDaysOfWeek.value)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = templateName.isNotBlank() && selectedDaysOfWeek.value.isNotEmpty(),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                ) {
                    Text("作成")
                }
            }

            // ICS ボタンはヘッダーに移動済み
        }
    }
}

private fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String =
    when (dayOfWeek) {
        DayOfWeek.MONDAY -> "月"
        DayOfWeek.TUESDAY -> "火"
        DayOfWeek.WEDNESDAY -> "水"
        DayOfWeek.THURSDAY -> "木"
        DayOfWeek.FRIDAY -> "金"
        DayOfWeek.SATURDAY -> "土"
        DayOfWeek.SUNDAY -> "日"
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
            onCreateTemplate = { _, _ -> },
            onImportFromIcs = {},
            isImportingIcs = false,
        )
    }
}
