package com.segnities007.templates.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.WeeklyTemplate
import com.segnities007.model.WeekDay

@Composable
fun WeeklyTemplateList(
    templates: List<WeeklyTemplate>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        templates.forEach { template ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            template.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "アイテム数: ${template.itemIds.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeeklyTemplateListPreview() {
    val dummyTemplates = listOf(
        WeeklyTemplate(
            id = 1,
            title = "月曜日の忘れ物",
            dayOfWeek = WeekDay.MONDAY,
            itemIds = listOf(1, 2, 3),
            itemCheckStates = mapOf(1 to false, 2 to true, 3 to false)
        ),
        WeeklyTemplate(
            id = 2,
            title = "火曜日の忘れ物",
            dayOfWeek = WeekDay.TUESDAY,
            itemIds = listOf(4, 5),
            itemCheckStates = mapOf(4 to true, 5 to false)
        )
    )

    WeeklyTemplateList(
        templates = dummyTemplates,
    )
}
