package com.segnities007.ui.card.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.ui.card.BaseCard
import com.segnities007.ui.indicator.CircularWavyProgressWithPercentage

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatCardWithPercentage(
    title: String,
    value: String,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    BaseCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            CircularWavyProgressWithPercentage(
                progress = progress,
                modifier = Modifier.size(80.dp),
            )
        }
    }
}

@Composable
@Preview
private fun StatCardWithPercentagePreview() {
    StatCardWithPercentage(title = "本日の達成", value = "8/12", progress = 8f/12f)
}
