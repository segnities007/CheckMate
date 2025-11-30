package com.segnities007.usecase.user

import com.segnities007.repository.UserRepository

/**
 * アカウントを作成するUse Case
 */
class CreateAccountUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            userRepository.createAccount()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
