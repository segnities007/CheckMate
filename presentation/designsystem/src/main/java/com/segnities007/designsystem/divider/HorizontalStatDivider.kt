package com.segnities007.designsystem.divider

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HorizontalStatDivider(
    label: String,
    itemCount: Int,
    checkedCount: Int,
    modifier: Modifier = Modifier,
) {
    HorizontalBaseDivider(
        modifier = modifier,
        startContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        endContent = {
            Text(
                text = "$checkedCount/$itemCount",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    )
}

@Composable
@Preview
private fun HorizontalStatDividerPreview() {
    HorizontalStatDivider(
        label = "今日の持ち物",
        itemCount = 7,
        checkedCount = 3,
    )
}
