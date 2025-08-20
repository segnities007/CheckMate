package com.segnities007.items.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.segnities007.model.item.Item
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ItemCard(
    item: Item,
    modifier: Modifier = Modifier,
    onDeleteItem: (Item) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.clickable { expanded = !expanded }) {
            ItemCardImage(
                imagePath = item.imagePath,
                name = item.name,
                onDeleteClick = { showDeleteDialog = true }
            )

            ItemCardTitle(title = item.name)

            ItemCardDetails(expanded = expanded, item = item)
        }
    }

    DeleteItemDialog(
        show = showDeleteDialog,
        item = item,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            onDeleteItem(item)
            showDeleteDialog = false
        }
    )
}

@Composable
private fun ItemCardImage(
    imagePath: String,
    name: String,
    onDeleteClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        if (imagePath.isNotEmpty()) {
            AsyncImage(
                model = imagePath,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Text("No Image", style = MaterialTheme.typography.bodySmall)
            }
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Delete Item",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun ItemCardTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(12.dp)
    )
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ItemCardDetails(expanded: Boolean, item: Item) {
    AnimatedVisibility(visible = expanded) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text("カテゴリ: ${item.category}", style = MaterialTheme.typography.bodyMedium)
            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Text(
                text = "登録日時: ${item.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun DeleteItemDialog(
    show: Boolean,
    item: Item,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
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
        }
    )
}
