package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.DayOfWeek
import com.segnities007.model.WeeklyTemplate
import com.segnities007.ui.tag.CountTag

@Composable
fun TemplateCard(
    modifier: Modifier = Modifier,
    template: WeeklyTemplate,
    onClick: () -> Unit = {},
    endContent: @Composable () -> Unit = {},
) {
    val rowBrush = Brush.horizontalGradient(
        0.0f to Color.Transparent,
        1.0f to Color.Transparent,
    )

    BaseCard(
        modifier = modifier,
        brush = rowBrush,
        tagContent = { mod ->
            CountTag(
                modifier = mod,
                count = template.itemIds.size,
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TemplateIcon(template)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = template.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (template.daysOfWeek.isNotEmpty()) {
                    val daysText = template.daysOfWeek.toList()
                        .sortedBy { it.ordinal }
                        .joinToString("、") { getDayOfWeekDisplayName(it) }
                    Text(
                        text = daysText,
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            endContent()
            Spacer(Modifier.width(2.dp))
        }
    }
}

@Composable
private fun TemplateIcon(template: WeeklyTemplate) {
    Box(
        modifier = Modifier.size((64+16).dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Schedule,
            contentDescription = template.title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
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

@Composable
@Preview
private fun TemplateCardPreview() {
    val template = WeeklyTemplate(
        id = 1,
        title = "月水金セット",
        daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        itemIds = listOf(1, 2, 3),
    )
    TemplateCard(template = template, onClick = {}) {
    }
}

@Composable
@Preview
private fun TemplateCardWithDeleteButtonPreview() {
    val template = WeeklyTemplate(
        id = 1,
        title = "月水金セット",
        daysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        itemIds = listOf(1, 2, 3),
    )
    TemplateCard(template = template, onClick = {}) {
        IconButton(
            onClick = {},
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
        }
    }
}
