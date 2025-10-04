package com.segnities007.usecase.user

import com.segnities007.repository.UserRepository

/**
 * アカウント作成済みかをチェックするUse Case
 */
class IsAccountCreatedUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Boolean {
        return userRepository.isAccountCreated()
    }
}
