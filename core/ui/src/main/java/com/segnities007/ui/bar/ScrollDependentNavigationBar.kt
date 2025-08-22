package com.segnities007.ui.bar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.segnities007.navigation.HubRoute

@Composable
fun ScrollDependentNavigationBar(
    scrollState: ScrollState,
    currentHubRoute: HubRoute,
    onNavigate: (HubRoute) -> Unit,
    setNavigationBar: (@Composable () -> Unit) -> Unit,
) {
    val targetAlpha by remember {
        derivedStateOf {
            when {
                scrollState.value > 50 -> 0f // 50px以上スクロールしたら非表示
                else -> (1f - scrollState.value / 50f).coerceIn(0f, 1f) // 0-50pxの範囲でフェード
            }
        }
    }
    
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = 200),
        label = "navigationBarAlpha"
    )

    setNavigationBar {
        FloatingNavigationBar(
            alpha = alpha,
            currentHubRoute = currentHubRoute,
            onNavigate = onNavigate,
        )
    }
}
