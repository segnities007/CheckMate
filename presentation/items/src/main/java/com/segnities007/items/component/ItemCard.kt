package com.segnities007.items.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ItemCard(
    item: Item,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDetailDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable { showDetailDialog = true },
        shape = RoundedCornerShape(12.dp),
        elevation =
            CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp, // Material3 Expressive: より大きなエレベーション
                pressedElevation = 8.dp,
                focusedElevation = 4.dp,
                hoveredElevation = 6.dp,
            ),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // アイテム画像
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        getCategoryColor(item.category).copy(alpha = 0.1f),
                                        getCategoryColor(item.category).copy(alpha = 0.05f),
                                    ),
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                if (item.imagePath.isNotEmpty()) {
                    AsyncImage(
                        model = item.imagePath,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Inventory,
                        contentDescription = "Item Icon",
                        tint = getCategoryColor(item.category),
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            // アイテム情報
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                CategoryTag(category = item.category)
            }

            // 削除ボタン
            IconButton(
                onClick = onDelete,
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "削除",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    // 詳細情報ダイアログ
    if (showDetailDialog) {
        ItemDetailDialog(
            item = item,
            onDismiss = { showDetailDialog = false },
        )
    }
}

@Composable
private fun ItemCardIcon(
    imagePath: String,
    name: String,
) {
    Box(
        modifier =
            Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (imagePath.isNotEmpty()) {
            AsyncImage(
                model = imagePath,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory,
                    contentDescription = "Item Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ItemDetailDialog(
    item: Item,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // カテゴリタグ
                CategoryTag(category = item.category)

                // 説明
                if (item.description.isNotEmpty()) {
                    Column {
                        Text(
                            text = "メモ",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                // 登録日
                Column {
                    Text(
                        text = "登録日",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = item.createdAt.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("閉じる") }
        },
    )
}

@Composable
private fun DeleteItemDialog(
    show: Boolean,
    item: Item,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("アイテムの削除") },
        text = { Text("「${item.name}」を本当に削除しますか？") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("削除") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("キャンセル") }
        },
    )
}

@Composable
private fun CategoryTag(category: ItemCategory) {
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
