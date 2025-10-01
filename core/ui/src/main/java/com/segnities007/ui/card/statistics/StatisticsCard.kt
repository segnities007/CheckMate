package com.segnities007.ui.card.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.indicator.CircularProgressWithPercentage
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
fun StatisticsCard(
    itemsForToday: List<Item>,
    itemCheckStates: Map<Int, Boolean>,
    modifier: Modifier = Modifier,
) {
    val checkedCount = itemCheckStates.values.count { it }
    val totalCount = itemsForToday.size
    val progress = if (totalCount > 0) checkedCount.toFloat() / totalCount else 0f

    val categoryStats =
        itemsForToday
            .groupBy { it.category }
            .mapValues { (_, items) ->
                val categoryCheckedCount = items.count { item -> itemCheckStates[item.id] == true }
                val categoryTotalCount = items.size
                val categoryProgress = if (categoryTotalCount > 0) categoryCheckedCount.toFloat() / categoryTotalCount else 0f
                Triple(categoryCheckedCount, categoryTotalCount, categoryProgress)
            }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "今日の進捗",
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "$checkedCount / $totalCount",
                        fontSize = 32.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // small circular with percentage
                CircularProgressWithPercentage(progress = progress)
            }

            if (totalCount == 0) {
                Text(
                    text = "今日は予定がありません",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                return@Column
            }

            if (categoryStats.isNotEmpty()) {
                // Show top 3 categories by remaining items to keep things compact
                val sorted = categoryStats.entries
                    .sortedByDescending { (cat, stats) -> stats.second - stats.first }
                    .take(3)

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    sorted.forEach { (category, stats) ->
                        val (checked, total, p) = stats
                        CategoryProgressRow(
                            category = category,
                            checkedCount = checked,
                            totalCount = total,
                            progress = p,
                        )
                    }
                    val remaining = categoryStats.size - sorted.size
                    if (remaining > 0) {
                        Text(
                            text = "他 ${remaining}カテゴリ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun StatisticsCardPreview() {
    val items = listOf(
        Item(id = 1, name = "水筒", category = ItemCategory.DAILY_SUPPLIES),
        Item(id = 2, name = "教科書", category = ItemCategory.STUDY_SUPPLIES),
        Item(id = 3, name = "雨具", category = ItemCategory.WEATHER_SUPPLIES),
    )
    val checks = mapOf(1 to true, 2 to false, 3 to true)
    StatisticsCard(itemsForToday = items, itemCheckStates = checks)
}

@Composable
private fun CategoryProgressRow(
    category: ItemCategory,
    checkedCount: Int,
    totalCount: Int,
    progress: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = getCategoryDisplayName(category),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1.2f).height(6.dp).clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round,
        )

        Text(
            text = "$checkedCount/$totalCount",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun getCategoryDisplayName(category: ItemCategory): String =
    when (category) {
        ItemCategory.STUDY_SUPPLIES -> "学業用品"
        ItemCategory.DAILY_SUPPLIES -> "生活用品"
        ItemCategory.CLOTHING_SUPPLIES -> "衣類用品"
        ItemCategory.FOOD_SUPPLIES -> "食事用品"
        ItemCategory.HEALTH_SUPPLIES -> "健康用品"
        ItemCategory.BEAUTY_SUPPLIES -> "美容用品"
        ItemCategory.EVENT_SUPPLIES -> "イベント用品"
        ItemCategory.HOBBY_SUPPLIES -> "趣味用品"
        ItemCategory.TRANSPORT_SUPPLIES -> "交通用品"
        ItemCategory.CHARGING_SUPPLIES -> "充電用品"
        ItemCategory.WEATHER_SUPPLIES -> "天候対策用品"
        ItemCategory.ID_SUPPLIES -> "証明用品"
        ItemCategory.OTHER_SUPPLIES -> "その他用品"
    }
