package com.segnities007.setting.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.ui.button.RectangleButton
import com.segnities007.ui.divider.HorizontalDividerWithLabel

@Composable
internal fun DataButtons(
    onExport: () -> Unit,
    onImport: () -> Unit,
    onBackUp: () -> Unit,
    onRestore: () -> Unit,
) {
    val modifier = Modifier.fillMaxWidth()
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        HorizontalDividerWithLabel("Data")
        RectangleButton(
            modifier = modifier,
            text = "Export",
            endIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = {
                Log.d("DataButtons", "Export")
                onExport()
            },
        )
        RectangleButton(
            modifier = modifier,
            text = "Import",
            endIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = {
                onImport()
            },
        )
        RectangleButton(
            modifier = modifier,
            text = "Back Up",
            endIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = {
                // TODO
            },
        )
        RectangleButton(
            modifier = modifier,
            text = "Restore",
            endIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = {
                // TODO
            },
        )
    }
}
