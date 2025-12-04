package com.segnities007.login.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.usecase.user.CreateAccountUseCase
import com.segnities007.usecase.user.LoginWithGoogleUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
) : BaseViewModel<LoginIntent, LoginState, LoginEffect>(LoginState) {

    override suspend fun handleIntent(intent: LoginIntent) {
        when (intent) {
            LoginIntent.ContinueWithGoogle -> continueWithGoogle()
            LoginIntent.ContinueWithNothing -> continueWithNothing()
        }
    }

    private fun continueWithGoogle() {
        viewModelScope.launch {
            loginWithGoogleUseCase().fold(
                onSuccess = {
                    sendEffect { LoginEffect.NavigateToHub }
                },
                onFailure = {
                    sendEffect { LoginEffect.ShowToast("ログインに失敗しました") }
                }
            )
        }
    }

    private fun continueWithNothing() {
        viewModelScope.launch {
            createAccountUseCase().fold(
                onSuccess = {
                    sendEffect { LoginEffect.NavigateToHub }
                },
                onFailure = {
                    sendEffect { LoginEffect.ShowToast("アカウント作成に失敗しました") }
                }
            )
        }
    }
}
