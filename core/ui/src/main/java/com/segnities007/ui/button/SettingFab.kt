package com.segnities007.ui.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingFab(
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(if (expanded) 90f else 0f)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 展開時のメニュー
        AnimatedVisibility(visible = expanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FabActionButton(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                    onClick = {
                        onAdd()
                        expanded = false
                    },
                )
                FabActionButton(
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
                    onClick = {
                        onEdit()
                        expanded = false
                    },
                )
                FabActionButton(
                    icon = { Icon(Icons.Default.Delete, contentDescription = "Delete") },
                    onClick = {
                        onDelete()
                        expanded = false
                    },
                )
            }
        }

        // メインのFAB
        FloatingActionButton(
            onClick = { expanded = !expanded },
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Menu",
                modifier = Modifier.rotate(rotation),
            )
        }
    }
}

@Preview
@Composable
fun FabPreview() {
    MaterialTheme {
        SettingFab(
            onAdd = {},
            onEdit = {},
            onDelete = {},
        )
    }
}

@Composable
private fun FabActionButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    SmallFloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
    ) {
        icon()
    }
}
