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
import com.segnities007.ui.dialog.ItemDetailDialog
import com.segnities007.ui.tag.CategoryTag
import com.segnities007.ui.util.getCategoryColor
import com.segnities007.ui.util.getCategoryDisplayName
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