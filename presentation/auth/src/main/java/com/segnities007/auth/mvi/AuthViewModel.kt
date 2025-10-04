package com.segnities007.auth.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.navigation.AuthRoute
import com.segnities007.navigation.Route
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.ui.mvi.MviState
import com.segnities007.usecase.user.IsAccountCreatedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class AuthViewModel(
    private val isAccountCreatedUseCase: IsAccountCreatedUseCase,
) : BaseViewModel<AuthIntent, MviState, AuthEffect>(object : MviState {}),
    KoinComponent {
    override suspend fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Navigate -> navigate(intent)
            is AuthIntent.ShowToast -> showToast(intent)
            AuthIntent.CheckAccount -> checkAccount()
            is AuthIntent.TopNavigate -> topNavigate(intent)
        }
    }

    init {
        sendIntent(AuthIntent.CheckAccount)
    }

    private fun init() {
        // kept for compatibility but not used; checkAccount handles the logic
    }

    private suspend fun checkAccount() {
        val result = isAccountCreatedUseCase()
        if (result) {
            topNavigate(AuthIntent.TopNavigate(Route.Hub))
        } else {
            navigate(
                AuthIntent.Navigate(AuthRoute.Login),
            )
        }
    }

    private fun navigate(intent: AuthIntent.Navigate) {
        sendEffect { AuthEffect.Navigate(intent.authRoute) }
    }

    private fun showToast(intent: AuthIntent.ShowToast) {
        sendEffect { AuthEffect.ShowToast(intent.message) }
    }

    private fun topNavigate(intent: AuthIntent.TopNavigate) {
        sendEffect { AuthEffect.TopNavigate(intent.route) }
    }
}
