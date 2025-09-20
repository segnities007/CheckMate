package com.segnities007.ui.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.navigation.HubRoute

@Composable
fun FloatingNavigationBar(
    alpha: Float = 1f,
    currentHubRoute: HubRoute,
    onNavigate: (HubRoute) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FloatingActionBarUi(
            alpha = alpha,
            currentHubRoute = currentHubRoute,
            onNavigate = onNavigate,
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun FloatingActionBarUi(
    alpha: Float,
    currentHubRoute: HubRoute,
    onNavigate: (HubRoute) -> Unit,
) {
    val info =
        mapOf(
            HubRoute.Home to listOf(Icons.Filled.Home, Icons.Outlined.Home),
            HubRoute.Items to listOf(Icons.Filled.Category, Icons.Outlined.Category),
            HubRoute.Dashboard to listOf(Icons.Filled.SpaceDashboard, Icons.Outlined.SpaceDashboard),
            HubRoute.Templates to listOf(Icons.Filled.ContentPaste, Icons.Outlined.ContentPaste),
            HubRoute.Setting to listOf(Icons.Filled.Settings, Icons.Outlined.Settings),
        )

    if (alpha > 0f) {
        Surface(
            shape = CircleShape,
            shadowElevation = 12.dp,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha),
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                info.forEach { (route, icons) ->
                    NavItemButton(
                        alpha = alpha,
                        selectedIcon = icons[0],
                        unselectedIcon = icons[1],
                        selected = currentHubRoute == route,
                        onClick = { onNavigate(route) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItemButton(
    alpha: Float,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
    ) {
        Icon(
            modifier = Modifier.size(36.dp),
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = "",
            tint =
                when (selected) {
                    true -> MaterialTheme.colorScheme.primary.copy(alpha = alpha)
                    false -> MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.4f)
                },
        )
    }
}

@Composable
@Preview
private fun FloatingNavigationBarPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
        FloatingNavigationBar(
            currentHubRoute = HubRoute.Home,
            onNavigate = {},
        )
    }
}
