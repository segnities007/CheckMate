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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.common.keys.NavKeys
import com.segnities007.common.keys.NavKeys.Hub

@Composable
fun FloatingNavigationBar(
    alpha: Float = 1f,
    currentHubRoute: NavKeys,
    onNavigate: (NavKeys) -> Unit,
) {
    Column(
        modifier = Modifier
            .graphicsLayer(alpha = alpha)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FloatingActionBarUi(
            currentHubRoute = currentHubRoute,
            onNavigate = onNavigate,
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun FloatingActionBarUi(
    currentHubRoute: NavKeys,
    onNavigate: (NavKeys) -> Unit,
) {
    val info =
        mapOf(
            Hub.HomeKey to listOf(Icons.Filled.Home, Icons.Outlined.Home),
            Hub.Items.ListKey to listOf(Icons.Filled.Category, Icons.Outlined.Category),
            Hub.DashboardKey to listOf(Icons.Filled.SpaceDashboard, Icons.Outlined.SpaceDashboard),
            Hub.Template.ListKey to listOf(Icons.Filled.ContentPaste, Icons.Outlined.ContentPaste),
            Hub.SettingKey to listOf(Icons.Filled.Settings, Icons.Outlined.Settings),
        )

    val brush =
        Brush.verticalGradient(
            colors =
                listOf(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.primary.copy(0.2f),
                ),
        )

    Row(
        modifier =
            Modifier
                .shadow(2.dp, CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .background(brush, CircleShape)
                .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        info.forEach { (route, icons) ->
            NavItemButton(
                selectedIcon = icons[0],
                unselectedIcon = icons[1],
                selected = currentHubRoute == route,
                onClick = {
                    onNavigate(route)
                },
            )
        }
    }
}

@Composable
private fun NavItemButton(
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
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.onSurface.copy(0.2f)
                },
        )
    }
}

@Composable
@Preview
private fun FloatingNavigationBarPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        FloatingNavigationBar(
            currentHubRoute = Hub.HomeKey,
            onNavigate = {},
        )
    }
}
