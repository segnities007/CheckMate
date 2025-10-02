package com.segnities007.ui.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FloatingConfirmBar(
    alpha: Float = 1f,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .graphicsLayer(alpha = alpha)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FloatingActionBarUi(
            onConfirm = onConfirm,
            onCancel = onCancel,
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun FloatingActionBarUi(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
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
        IconButton(
            onClick = onCancel,
            modifier = Modifier.size(64.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel",
                modifier = Modifier.size(32.dp),
                tint = Color.Red,
            )
        }
        IconButton(
            onClick = onConfirm,
            modifier = Modifier.size(64.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Confirm",
                modifier = Modifier
                    .size(32.dp),
                tint = Color.Blue,
               )
        }
    }
}

@Preview
@Composable
fun FloatingConfirmBarPreview() {
    FloatingConfirmBar(
        onConfirm = {},
        onCancel = {},
    )
}
