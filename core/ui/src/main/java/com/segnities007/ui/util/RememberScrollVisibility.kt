package com.segnities007.ui.util

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.*

@Composable
fun rememberScrollVisibility(
    scrollState: ScrollState,
    threshold: Int = 10, // 多少のスクロール誤差を無視
): State<Boolean> {
    var previous by remember { mutableIntStateOf(0) }

    val isVisible =
        remember {
            derivedStateOf {
                val current = scrollState.value
                val delta = current - previous
                val visible = delta < 0 || current == 0
                if (kotlin.math.abs(delta) > threshold) {
                    previous = current
                }
                visible
            }
        }

    return isVisible
}
