package com.ricardocosta.api.bank.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    @Column("internal_id")
    val internalId: Long = 0,

    @Column("public_id")
    val publicId: String,

    val username: String
)
