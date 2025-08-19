package com.segnities007.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ElevatedCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor =
                    if (checked) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 画像表示
            if (!item.imagePath.isNullOrEmpty()) {
                AsyncImage(
                    model = item.imagePath,
                    contentDescription = item.name,
                    modifier =
                        Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)),
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No Image", textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 名前・カテゴリ・説明
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                item.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                    )
                }
            }

            // チェックボックス
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview(showBackground = true)
@Composable
fun ItemCardPreview() {
    val dummyItem =
        Item(
            id = 1,
            name = "水筒",
            description = "飲み物を忘れない",
            category = ItemCategory.OTHER_SUPPLIES,
            imagePath = "https://placehold.co/80x80",
        )
    var checked by remember { mutableStateOf(false) }

    MaterialTheme {
        ItemCard(
            item = dummyItem,
            checked = checked,
            onCheckedChange = { checked = it },
        )
    }
}
