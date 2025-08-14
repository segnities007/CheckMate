package com.segnities007.ui.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.navigation.HubRoute

@Composable
fun FloatingNavigationBar(
    currentHubRoute: HubRoute,
    onNavigate: (HubRoute) -> Unit
){

    val info = mapOf(
        HubRoute.Home to listOf(Icons.Filled.Home, Icons.Outlined.Home),
        HubRoute.Items to listOf(Icons.Filled.Category, Icons.Outlined.Category),
        HubRoute.Dashboard to listOf(Icons.Filled.SpaceDashboard, Icons.Outlined.SpaceDashboard),
        HubRoute.Templates to listOf(Icons.Filled.ContentPaste, Icons.Outlined.ContentPaste),
        HubRoute.Setting to listOf(Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        info.forEach { (route, icons) ->
            NavItemButton(
                selectedIcon = icons[0],
                unselectedIcon = icons[1],
                label = route.toString().substringAfterLast('.'),
                selected = currentHubRoute == route,
                onClick = { onNavigate(route) }
            )
        }
    }
}

@Composable
private fun NavItemButton(
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground
            )
            .size(64.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.size(36.dp),
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.background
            )
            if (selected) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}


@Composable
@Preview
private fun FloatingNavigationBarPreview(){
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ){
        FloatingNavigationBar(
            currentHubRoute = HubRoute.Home,
            onNavigate = {}
        )

    }
}