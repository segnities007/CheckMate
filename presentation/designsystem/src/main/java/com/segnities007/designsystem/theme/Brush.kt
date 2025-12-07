package com.segnities007.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush

val MaterialTheme.checkMateBackgroundBrush: Brush
    @Composable
    @ReadOnlyComposable
    get() = Brush.verticalGradient(
        colorStops = arrayOf(
            0f to colorScheme.primaryContainer,
            0.8f to colorScheme.primaryContainer,
            1f to colorScheme.primaryContainer.copy(0.5f)
        ),
    )
