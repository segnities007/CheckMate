package com.segnities007.login.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.ui.mvi.MviState
import com.segnities007.usecase.user.CreateAccountUseCase
import com.segnities007.usecase.user.LoginWithGoogleUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class LoginViewModel(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
) : BaseViewModel<
        LoginIntent,
        MviState,
        LoginEffect,
    >(object : MviState {}),
    KoinComponent {
    override suspend fun handleIntent(intent: LoginIntent) {
        when (intent) {
            LoginIntent.ContinueWithGoogle -> continueWithGoogle()
            LoginIntent.ContinueWithNothing -> continueWithNothing()
        }
    }

    private suspend fun continueWithGoogle() {
        val result = loginWithGoogleUseCase()
        result.fold(
            onSuccess = {
                sendEffect { LoginEffect.NavigateToHub }
            },
            onFailure = {
                sendEffect { LoginEffect.ShowToast("ログインに失敗しました") }
            }
        )
    }

    private suspend fun continueWithNothing() {
        val result = createAccountUseCase()
        result.fold(
            onSuccess = {
                sendEffect { LoginEffect.NavigateToHub }
            },
            onFailure = {
                sendEffect { LoginEffect.ShowToast("アカウント作成に失敗しました") }
            }
        )
    }
}
