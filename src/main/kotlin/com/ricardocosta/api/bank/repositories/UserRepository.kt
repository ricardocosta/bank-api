package com.ricardocosta.api.bank.repositories

import com.ricardocosta.api.bank.domain.User
import com.ricardocosta.api.bank.domain.views.UserDetailsView
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : R2dbcRepository<User, Long> {
    fun findByPublicId(publicId: String): Mono<UserDetailsView>
}
