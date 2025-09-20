package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun DeletableItemCard(
    item: Item,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showDetailDialog = remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth().clickable { showDetailDialog.value = true },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(
                    Brush.verticalGradient(
                        colors = listOf(getCategoryColor(item.category).copy(alpha = 0.1f), getCategoryColor(item.category).copy(alpha = 0.05f)),
                    ),
                ),
                contentAlignment = Alignment.Center,
            ) {
                if (item.imagePath.isNotEmpty()) {
                    AsyncImage(model = item.imagePath, contentDescription = item.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(imageVector = Icons.Filled.Inventory, contentDescription = "Item Icon", tint = getCategoryColor(item.category), modifier = Modifier.size(32.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                CategoryTag(category = item.category)
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }

    if (showDetailDialog.value) {
        ItemDetailDialog(item = item, onDismiss = { showDetailDialog.value = false })
    }
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun DeletableItemCardPreview() {
    DeletableItemCard(
        item = Item(id = 2, name = "教科書", category = ItemCategory.STUDY_SUPPLIES),
        onDelete = {},
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ItemDetailDialog(item: Item, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = item.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CategoryTag(category = item.category)
                if (item.description.isNotEmpty()) {
                    Column {
                        Text(text = "メモ", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = item.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Column {
                    Text(text = "登録日", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = item.createdAt.toString(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("閉じる") } },
    )
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
