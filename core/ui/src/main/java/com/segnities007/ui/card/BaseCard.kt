package com.segnities007.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.ui.tag.CategoryTag

@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    brush: Brush = Brush.verticalGradient(0.0f to Color.Transparent, 1.0f to Color.Transparent),
    alpha: Float = 0.6f,
    onCardClick: () -> Unit = {},
    tagContent: @Composable (modifier: Modifier) -> Unit = {},
    startContent: @Composable () -> Unit = {},
    centerContent: @Composable () -> Unit = {},
    endContent: @Composable () -> Unit = {},
) {

    Box(
        modifier = modifier
    ){
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = alpha),
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCardClick)
                    .background(brush = brush),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                startContent()
                centerContent()
                endContent()
            }
        }
        tagContent(
            Modifier.
            align(Alignment.TopStart)
                .offset(x = (-6).dp, y = (-12).dp),
        )
    }
}

@Composable
@Preview
private fun BaseCardPreview(){
    BaseCard(
        tagContent = { modifier ->
            CategoryTag(
                modifier = modifier,
                category = com.segnities007.model.item.ItemCategory.CLOTHING_SUPPLIES
            )
        }
    )
}