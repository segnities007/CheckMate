package com.segnities007.ui.card.calendar.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.ui.card.calendar.util.japaneseWeekday
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Composable
fun WeekdayLabelsRow(days: List<LocalDate>, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        days.forEach { date ->
            val label = japaneseWeekday(date.dayOfWeek)
            val color = when (date.dayOfWeek) {
                DayOfWeek.SUNDAY -> Color.Red
                DayOfWeek.SATURDAY -> Color.Blue
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    color = color,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}
