package com.segnities007.ui.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.segnities007.model.item.ItemCategory
import com.segnities007.ui.util.getCategoryColor
import com.segnities007.ui.util.getCategoryDisplayName

@Composable
fun CategoryTag(
    modifier: Modifier = Modifier,
    category: ItemCategory,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .background(getCategoryColor(category).copy(0.5f))
            .padding(vertical = 2.dp, horizontal = 6.dp),
    ) {
        Text(
            text = getCategoryDisplayName(category),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Preview
@Composable
fun CategoryTagPreview() {
    CategoryTag(category = ItemCategory.CLOTHING_SUPPLIES)
}


