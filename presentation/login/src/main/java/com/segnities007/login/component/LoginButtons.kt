package com.segnities007.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.segnities007.login.R
import com.segnities007.login.mvi.LoginIntent

@Composable
fun LoginButtons(
    sendIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoginButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Continue with Google",
            res = R.drawable.icons8_google,
        ) {
            sendIntent(LoginIntent.ContinueWithGoogle)
        }
        LoginButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Continue with nothing",
        ) {
            sendIntent(LoginIntent.ContinueWithNothing)
        }
    }
}
