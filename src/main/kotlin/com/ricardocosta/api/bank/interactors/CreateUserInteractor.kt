package com.ricardocosta.api.bank.interactors

import com.ricardocosta.api.bank.domain.views.UserDetailsView
import com.ricardocosta.api.bank.dto.CreateUserDTO
import com.ricardocosta.api.bank.repositories.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateUserInteractor(
    private val userRepository: UserRepository
) {
    fun call(u: CreateUserDTO): Mono<UserDetailsView> =
        userRepository
            .save(CreateUserDTO.toEntity(u))
            .map(UserDetailsView::fromUser)
}
