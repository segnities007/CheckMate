package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.segnities007.model.item.Item
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
fun CheckableItemCard(
    item: Item,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().height(100.dp).clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ItemImage(item.imagePath, item.name)
            Spacer(Modifier.width(12.dp))
            ItemTexts(item.name, item.description)
            Spacer(Modifier.weight(1f))
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
@Preview
private fun CheckableItemCardPreview() {
    CheckableItemCard(item = Item(id = 1, name = "財布"), checked = false, onCheckedChange = {})
}

@Composable
private fun ItemImage(imagePath: String, name: String) {
    if (imagePath.isNotEmpty()) {
        AsyncImage(
            model = imagePath,
            contentDescription = name,
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(8.dp),
            contentAlignment = Alignment.Center,
        ) { Text("No Image", textAlign = TextAlign.Center) }
    }
}

@Composable
private fun ItemTexts(name: String, description: String?) {
    Column {
        Text(text = name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 16.sp)
        if (!description.isNullOrEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
