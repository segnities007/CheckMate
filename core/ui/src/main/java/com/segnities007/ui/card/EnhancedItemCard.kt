package com.segnities007.ui.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.segnities007.model.item.Item
import com.segnities007.model.item.ItemCategory
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
fun EnhancedItemCard(
    item: Item,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 0.975f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale",
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth().scale(scale).clip(RoundedCornerShape(16.dp)).clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            EnhancedItemImage(imagePath = item.imagePath, name = item.name, category = item.category)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (item.description.isNotEmpty()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    )
                }
                CategoryTag(category = item.category)
            }
            EnhancedCheckbox(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun EnhancedItemCardPreview() {
    EnhancedItemCard(
        item = Item(id = 1, name = "水筒", description = "飲み物を忘れない", category = ItemCategory.DAILY_SUPPLIES),
        checked = false,
        onCheckedChange = {},
    )
}

@Composable
private fun EnhancedItemImage(imagePath: String, name: String, category: ItemCategory) {
    Box(
        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(
            Brush.verticalGradient(
                colors = listOf(getCategoryColor(category).copy(alpha = 0.1f), getCategoryColor(category).copy(alpha = 0.05f)),
            ),
        ),
        contentAlignment = Alignment.Center,
    ) {
        if (imagePath.isNotEmpty()) {
            AsyncImage(model = imagePath, contentDescription = name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Icon(imageVector = Icons.Filled.Inventory, contentDescription = "Item Icon", tint = getCategoryColor(category), modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
private fun CategoryTag(category: ItemCategory) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(getCategoryColor(category).copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp),
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

@Composable
private fun EnhancedCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "checkbox_scale",
    )

    Box(
        modifier = Modifier.size(48.dp).scale(scale).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (checked) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = if (checked) "Checked" else "Unchecked",
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )
    }
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
