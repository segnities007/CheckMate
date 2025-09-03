package com.segnities007.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserStatus(
    val id: String = "",
    val name: String = "NoName",
    val email: String = "NoEmail",
    val pictureUrl: String? = null,
)
