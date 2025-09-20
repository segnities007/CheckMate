package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.segnities007.model.item.ItemCategory

@Composable
fun CategoryGroupHeader(
    category: ItemCategory,
    itemCount: Int,
    checkedCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(getCategoryColor(category)),
            )

            Text(
                text = getCategoryDisplayName(category),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = "$checkedCount/$itemCount",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
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

private fun getCategoryColor(category: ItemCategory): Color =
    when (category) {
        ItemCategory.STUDY_SUPPLIES -> Color(0xFF2196F3)
        ItemCategory.DAILY_SUPPLIES -> Color(0xFF4CAF50)
        ItemCategory.CLOTHING_SUPPLIES -> Color(0xFF9C27B0)
        ItemCategory.FOOD_SUPPLIES -> Color(0xFFFF9800)
        ItemCategory.HEALTH_SUPPLIES -> Color(0xFFF44336)
        ItemCategory.BEAUTY_SUPPLIES -> Color(0xFFE91E63)
        ItemCategory.EVENT_SUPPLIES -> Color(0xFF673AB7)
        ItemCategory.HOBBY_SUPPLIES -> Color(0xFF3F51B5)
        ItemCategory.TRANSPORT_SUPPLIES -> Color(0xFF009688)
        ItemCategory.CHARGING_SUPPLIES -> Color(0xFF795548)
        ItemCategory.WEATHER_SUPPLIES -> Color(0xFF607D8B)
        ItemCategory.ID_SUPPLIES -> Color(0xFF8BC34A)
        ItemCategory.OTHER_SUPPLIES -> Color(0xFF607D8B)
    }

@Composable
@Preview
private fun CategoryGroupHeaderPreview() {
    CategoryGroupHeader(
        category = ItemCategory.STUDY_SUPPLIES,
        itemCount = 7,
        checkedCount = 3,
    )
}
