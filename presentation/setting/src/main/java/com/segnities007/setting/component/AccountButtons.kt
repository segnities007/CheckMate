package com.segnities007.setting.component

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
internal fun AccountButtons() {
    val modifier = Modifier.fillMaxWidth()
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        HorizontalDividerWithLabel("Account")
        RectangleButton(
            modifier = modifier,
            text = "Linking",
            endIcon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            onClick = {
                // TODO
            },
        )
    }
}
