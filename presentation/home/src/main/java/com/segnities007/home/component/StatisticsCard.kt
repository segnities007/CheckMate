package com.segnities007.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.indicator.CircularProgressWithPercentage

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

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // メイン進捗
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "達成数",
                        fontSize = 12.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "$checkedCount/$totalCount",
                        fontSize = 36.sp,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                CircularProgressWithPercentage(progress = progress)
            }

            // カテゴリ別統計
            if (categoryStats.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    categoryStats.forEach { (category, stats) ->
                        val (checked, total, progress) = stats
                        CategoryProgressRow(
                            category = category,
                            checkedCount = checked,
                            totalCount = total,
                            progress = progress,
                        )
                    }
                }
            }
        }
    }
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = getCategoryDisplayName(category),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "$checkedCount/$totalCount",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .width(80.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round,
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
