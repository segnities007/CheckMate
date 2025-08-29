package com.segnities007.templates.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.DayOfWeek
import com.segnities007.model.item.ItemCategory

fun getDayOfWeekDisplayName(dayOfWeek: DayOfWeek): String =
    when (dayOfWeek) {
        DayOfWeek.MONDAY -> "月"
        DayOfWeek.TUESDAY -> "火"
        DayOfWeek.WEDNESDAY -> "水"
        DayOfWeek.THURSDAY -> "木"
        DayOfWeek.FRIDAY -> "金"
        DayOfWeek.SATURDAY -> "土"
        DayOfWeek.SUNDAY -> "日"
    }

fun getCategoryColor(category: ItemCategory): Color =
    when (category) {
        ItemCategory.STUDY_SUPPLIES -> Color(0xFF2196F3) // Blue - 学業用品
        ItemCategory.DAILY_SUPPLIES -> Color(0xFF4CAF50) // Green - 生活用品
        ItemCategory.CLOTHING_SUPPLIES -> Color(0xFF9C27B0) // Purple - 衣類用品
        ItemCategory.FOOD_SUPPLIES -> Color(0xFFFF9800) // Orange - 食事用品
        ItemCategory.HEALTH_SUPPLIES -> Color(0xFFF44336) // Red - 健康用品
        ItemCategory.BEAUTY_SUPPLIES -> Color(0xFFE91E63) // Pink - 美容用品
        ItemCategory.EVENT_SUPPLIES -> Color(0xFF673AB7) // Deep Purple - イベント用品
        ItemCategory.HOBBY_SUPPLIES -> Color(0xFF3F51B5) // Indigo - 趣味用品
        ItemCategory.TRANSPORT_SUPPLIES -> Color(0xFF009688) // Teal - 交通用品
        ItemCategory.CHARGING_SUPPLIES -> Color(0xFF795548) // Brown - 充電用品
        ItemCategory.WEATHER_SUPPLIES -> Color(0xFF607D8B) // Blue Grey - 天候対策用品
        ItemCategory.ID_SUPPLIES -> Color(0xFF8BC34A) // Light Green - 証明用品
        ItemCategory.OTHER_SUPPLIES -> Color(0xFF607D8B) // Blue Grey - その他用品
    }

fun getCategoryDisplayName(category: ItemCategory): String =
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

@Composable
fun CategoryTag(category: ItemCategory) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(getCategoryColor(category).copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = getCategoryDisplayName(category),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = getCategoryColor(category),
            fontSize = 12.sp,
        )
    }
}
