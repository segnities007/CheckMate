package com.segnities007.designsystem.indicator

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircularWavyProgressWithPercentage(
    progress: Float,
    modifier: Modifier = Modifier.size(120.dp),
) {
    BoxWithConstraints(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        val size = maxWidth
        val stroke = size * 0.12f
        val fontSize = size * 0.2f

        CircularWavyProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            stroke = Stroke(width = 16f, cap = StrokeCap.Round),
            wavelength = 12.dp,
            progress = { progress },
        )

        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = fontSize.value.sp,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
fun CircularWavyProgressWithPercentagePreview() {
    CircularWavyProgressWithPercentage(progress = 0.75f)
}