package com.segnities007.ui.divider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HorizontalDividerWithLabel(label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            Modifier.weight(2f),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        HorizontalDivider(
            Modifier.weight(8f),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
@Preview
private fun HorizontalDividerWithLabelPreview() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.White),
    ) {
        HorizontalDividerWithLabel(label = "Label")
    }
}
