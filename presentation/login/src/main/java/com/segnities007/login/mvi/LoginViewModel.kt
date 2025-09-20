package com.segnities007.login.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.repository.UserRepository
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.ui.mvi.MviState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class LoginViewModel(
    private val userRepository: UserRepository,
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
        try {
            withContext(Dispatchers.IO) { userRepository.loginWithGoogle() }
            sendEffect { LoginEffect.NavigateToHub }
        } catch (_: Exception) {
            sendEffect { LoginEffect.ShowToast("ログインに失敗しました") }
        }
    }

    private suspend fun continueWithNothing() {
        withContext(Dispatchers.IO) { userRepository.createAccount() }
        sendEffect { LoginEffect.NavigateToHub }
    }
}
