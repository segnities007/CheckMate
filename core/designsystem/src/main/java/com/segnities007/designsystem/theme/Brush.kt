package com.segnities007.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val MaterialTheme.checkMateBackgroundBrush: Brush
    @Composable
    @ReadOnlyComposable
    get() = Brush.verticalGradient(
        colors = listOf(
            colorScheme.primaryContainer,
            colorScheme.primaryContainer,
            Color.Yellow.copy(0.3f),
        ),
    )
