package com.segnities007.auth.mvi

import androidx.lifecycle.viewModelScope
import com.segnities007.navigation.AuthRoute
import com.segnities007.navigation.Route
import com.segnities007.repository.UserRepository
import com.segnities007.ui.mvi.BaseViewModel
import com.segnities007.ui.mvi.MviState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class AuthViewModel(
    private val userRepository: UserRepository,
) : BaseViewModel<AuthIntent, MviState, AuthEffect>(object : MviState {}),
    KoinComponent {
    override suspend fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Navigate -> navigate(intent)
            is AuthIntent.ShowToast -> showToast(intent)
            is AuthIntent.TopNavigate -> topNavigate(intent)
        }
    }

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepository.isAccountCreated()
            if (result) {
                topNavigate(AuthIntent.TopNavigate(Route.Hub))
            } else {
                navigate(
                    AuthIntent.Navigate(AuthRoute.Login),
                )
            }
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
