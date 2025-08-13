package com.segnities007.model

data class UserStatus(
    val id: String = "",
    val name: String?,
    val email: String?,
    val pictureUrl: String?
){
    companion object {
        fun simpleMock(): UserStatus = UserStatus(
            id = "id",
            name = "name",
            email = "email",
            pictureUrl = "picture_url"
        )
    }
}