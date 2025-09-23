package com.segnities007.ui.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun CountTag(
    modifier: Modifier = Modifier,
    count: Int,
){
    Box(
        modifier =
            modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .background(MaterialTheme.colorScheme.primary.copy(0.5f))
                .padding(vertical = 2.dp, horizontal = 6.dp),
    ) {
        Text(
            text = "${count}アイテム",
            fontSize = 10.sp,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview
@Composable
fun CountTagPreview() {
    CountTag(
        count = 3
    )
}
