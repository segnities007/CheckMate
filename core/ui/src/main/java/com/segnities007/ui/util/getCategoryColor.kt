package com.segnities007.ui.util

import androidx.compose.ui.graphics.Color
import com.segnities007.model.item.ItemCategory

internal fun getCategoryColor(category: ItemCategory): Color =
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
