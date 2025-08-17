package com.segnities007.repository

import com.segnities007.model.UserStatus
import com.segnities007.remote.Auth
import com.segnities007.repository.UserRepositoryImpl

class UserRepositoryImpl(
    private val auth: Auth,
) : UserRepository {
    override suspend fun loginWithGoogle() {
        auth.loginWithGoogle()
        auth.markAccountCreated()
    }

    override suspend fun isAccountCreated(): Boolean = auth.isAccountCreated()

    override suspend fun createAccount() {
        auth.markAccountCreated()
    }

    override suspend fun getUserStatus(): UserStatus = auth.getUserStatus() ?: UserStatus()
}
