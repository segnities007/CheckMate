package com.segnities007.usecase.user

import com.segnities007.repository.UserRepository

/**
 * アカウント作成済みかをチェックするUse Case
 */
class IsAccountCreatedUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return try {
            val isCreated = userRepository.isAccountCreated()
            Result.success(isCreated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
