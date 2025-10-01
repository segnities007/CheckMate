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
fun ItemCard(
    modifier: Modifier = Modifier,
    item: Item,
    isChecked: Boolean? = null,
    onCardClick: () -> Unit = {},
    endContent: @Composable () -> Unit = {},
) {
    val backgroundAlpha = 0.7f

    val rowBrush = when(isChecked){
        false -> Brush.horizontalGradient(
            0.0f to Color.Transparent,
            1.0f to MaterialTheme.colorScheme.surface.copy(backgroundAlpha),
        )
        else -> Brush.horizontalGradient(
            0.0f to Color.Transparent,
            1.0f to getCategoryColor(item.category),
        )
    }
    Box(
        modifier = modifier
    ){
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(0.3f),
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onCardClick).background(brush = rowBrush),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ItemIcon(item)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(text = item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = item.description, fontSize = 12.sp, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.secondary)
                }
                endContent()
                Spacer(Modifier.width(2.dp))
            }
        }
        CategoryTag(
            modifier = Modifier.
            align(Alignment.TopStart)
                .offset(x = (-6).dp, y = (-12).dp),
            category = item.category
        )
    }
}

@Composable
private fun ItemIcon(item: Item){
    Box(
        modifier = Modifier.size((64+16).dp),
        contentAlignment = Alignment.Center,
    ) {
        when(item.imagePath.isNotEmpty()){
            true -> AsyncImage(
                model = item.imagePath,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            false -> Icon(
                imageVector = Icons.Filled.Inventory,
                contentDescription = Icons.Filled.Inventory.toString(),
                tint = getCategoryColor(item.category),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun ItemCardPreview() {
    ItemCard(
        item = Item(id = 2, name = "教科書", category = ItemCategory.STUDY_SUPPLIES),
        isChecked = true,
        onCardClick = {},
    ){
    }
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun ItemCardWithDeleteButtonPreview() {
    ItemCard(
        item = Item(id = 2, name = "教科書", category = ItemCategory.STUDY_SUPPLIES),
        isChecked = false,
        onCardClick = {},
    ){
        IconButton(
            onClick = {},
            modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
        }
    }
}