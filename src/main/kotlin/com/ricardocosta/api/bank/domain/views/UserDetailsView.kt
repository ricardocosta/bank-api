package com.ricardocosta.api.bank.domain.views

import com.ricardocosta.api.bank.domain.User

data class UserDetailsView(
    val publicId: String,
    val username: String
) {
    companion object {
        fun fromUser(u: User): UserDetailsView = UserDetailsView(u.publicId, u.username)
    }
}
