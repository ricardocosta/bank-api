package com.ricardocosta.api.bank.repositories

import com.ricardocosta.api.bank.ContainerBackedTest
import com.ricardocosta.api.bank.domain.User
import com.ricardocosta.api.bank.domain.views.UserDetailsView
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import reactor.test.StepVerifier

@DataR2dbcTest
class UserRepositoryTests : ContainerBackedTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `#findByPublicId - when there is a matching public id - returns the User`() {
        val existingUser = User(publicId = "id_1", username = "test")
        val expectedResult = UserDetailsView(publicId = "id_1", username = "test")

        StepVerifier.create(
            userRepository.save(existingUser).then(userRepository.findByPublicId("id_1"))
        ).expectNext(expectedResult).verifyComplete()
    }

    @Test
    fun `#findByPublicId - when there is no matching public id - does not return the User`() {
        StepVerifier.create(
            userRepository.findByPublicId("id_1")
        ).verifyComplete()
    }
}
