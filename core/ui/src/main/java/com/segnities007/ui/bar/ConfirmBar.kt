package com.segnities007.ui.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.navigation.HubRoute

@Composable
fun ConfirmBar(
    alpha: Float = 1f,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = CircleShape,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Cancel - Material3 Expressive
                FloatingActionButton(
                    onClick = onCancel,
                    modifier = Modifier.size(64.dp), // Material3 Expressive: より大きなサイズ
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = alpha),
                    contentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = alpha),
                    elevation =
                        FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp, // Material3 Expressive: より大きなエレベーション
                            pressedElevation = 8.dp,
                            focusedElevation = 4.dp,
                            hoveredElevation = 6.dp,
                        ),
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        modifier = Modifier.size(32.dp), // Material3 Expressive: より大きなアイコン
                        tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = alpha),
                    )
                }

                // Confirm - Material3 Expressive
                FloatingActionButton(
                    onClick = onConfirm,
                    modifier = Modifier.size(64.dp), // Material3 Expressive: より大きなサイズ
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha),
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alpha),
                    elevation =
                        FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp, // Material3 Expressive: より大きなエレベーション
                            pressedElevation = 8.dp,
                            focusedElevation = 4.dp,
                            hoveredElevation = 6.dp,
                        ),
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        modifier = Modifier.size(32.dp), // Material3 Expressive: より大きなアイコン
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = alpha),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Preview
@Composable
fun ConfirmBarPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
    ) {
    }
    ConfirmBar(
        onConfirm = {},
        onCancel = {},
    )
}
