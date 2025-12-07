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
import kotlinx.datetime.DayOfWeek
import com.segnities007.model.item.ItemCategory

import com.segnities007.designsystem.theme.CategoryBeauty
import com.segnities007.designsystem.theme.CategoryCharging
import com.segnities007.designsystem.theme.CategoryClothing
import com.segnities007.designsystem.theme.CategoryDaily
import com.segnities007.designsystem.theme.CategoryEvent
import com.segnities007.designsystem.theme.CategoryFood
import com.segnities007.designsystem.theme.CategoryHealth
import com.segnities007.designsystem.theme.CategoryHobby
import com.segnities007.designsystem.theme.CategoryId
import com.segnities007.designsystem.theme.CategoryOther
import com.segnities007.designsystem.theme.CategoryStudy
import com.segnities007.designsystem.theme.CategoryTransport
import com.segnities007.designsystem.theme.CategoryWeather
import com.segnities007.designsystem.theme.Dimens

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
        ItemCategory.STUDY_SUPPLIES -> CategoryStudy
        ItemCategory.DAILY_SUPPLIES -> CategoryDaily
        ItemCategory.CLOTHING_SUPPLIES -> CategoryClothing
        ItemCategory.FOOD_SUPPLIES -> CategoryFood
        ItemCategory.HEALTH_SUPPLIES -> CategoryHealth
        ItemCategory.BEAUTY_SUPPLIES -> CategoryBeauty
        ItemCategory.EVENT_SUPPLIES -> CategoryEvent
        ItemCategory.HOBBY_SUPPLIES -> CategoryHobby
        ItemCategory.TRANSPORT_SUPPLIES -> CategoryTransport
        ItemCategory.CHARGING_SUPPLIES -> CategoryCharging
        ItemCategory.WEATHER_SUPPLIES -> CategoryWeather
        ItemCategory.ID_SUPPLIES -> CategoryId
        ItemCategory.OTHER_SUPPLIES -> CategoryOther
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
                .clip(RoundedCornerShape(Dimens.CornerSmall))
                .background(getCategoryColor(category).copy(alpha = 0.1f))
                .padding(horizontal = Dimens.PaddingSmall, vertical = Dimens.PaddingExtraSmall),
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
