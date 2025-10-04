package com.segnities007.usecase.user

import com.segnities007.model.UserStatus
import com.segnities007.repository.UserRepository

/**
 * ユーザーステータスを取得するUse Case
 */
class GetUserStatusUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<UserStatus> {
        return try {
            val status = userRepository.getUserStatus()
            Result.success(status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
