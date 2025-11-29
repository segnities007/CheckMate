package com.segnities007.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.segnities007.login.component.LoginButtons
import com.segnities007.login.component.LoginTitles
import com.segnities007.login.mvi.LoginEffect
import com.segnities007.login.mvi.LoginIntent
import com.segnities007.login.mvi.LoginViewModel
import com.segnities007.navigation.NavKey
import org.koin.compose.koinInject

import com.segnities007.ui.scaffold.CheckMateScaffold

@Composable
fun LoginScreen(topNavigate: (NavKey) -> Unit) {
    val loginViewModel: LoginViewModel = koinInject()

    LaunchedEffect(Unit) {
        loginViewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHub -> {
                    topNavigate(NavKey.Hub)
                }
                is LoginEffect.ShowToast -> {
                    // TODO
                }
            }
        }
    }

    CheckMateScaffold {
        LoginContent(sendIntent = loginViewModel::sendIntent)
    }
}

@Composable
private fun LoginContent(sendIntent: (LoginIntent) -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        LoginTitles()
        Spacer(modifier = Modifier.weight(1f))
        LoginButtons(sendIntent)
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
@Preview
private fun LoginContentPreview() {
    LoginContent(
        sendIntent = {},
    )
}
