package com.segnities007.usecase.user

import com.segnities007.model.UserStatus
import com.segnities007.repository.UserRepository

/**
 * ユーザーステータスを取得するUse Case
 */
class GetUserStatusUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserStatus {
        return userRepository.getUserStatus()
    }
}
