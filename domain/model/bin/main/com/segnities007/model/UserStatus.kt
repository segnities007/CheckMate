package com.segnities007.model


import kotlinx.serialization.Serializable

@Serializable

data class UserStatus(
    val id: String = "",
    val name: String = "NoName",
    val email: String = "NoEmail",
    val pictureUrl: String? = null,
)
