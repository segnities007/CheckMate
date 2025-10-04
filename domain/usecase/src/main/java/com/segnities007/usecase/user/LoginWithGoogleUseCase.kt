package com.segnities007.usecase.user

import com.segnities007.repository.UserRepository

/**
 * GoogleアカウントでログインするUse Case
 */
class LoginWithGoogleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            userRepository.loginWithGoogle()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
