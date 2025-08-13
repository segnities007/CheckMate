package com.segnities007.repository

import com.segnities007.model.UserStatus

interface UserRepository {
    suspend fun loginWithGoogle()
    suspend fun isAccountCreated(): Boolean
    suspend fun createAccount()
    suspend fun getUserStatus(): UserStatus

}