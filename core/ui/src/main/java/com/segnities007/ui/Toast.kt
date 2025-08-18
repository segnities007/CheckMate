package com.segnities007.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun Toast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    val context = LocalContext.current
    Toast.makeText(context, message, duration).show()
}
