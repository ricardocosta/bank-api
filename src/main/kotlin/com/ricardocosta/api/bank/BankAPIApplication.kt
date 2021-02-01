package com.ricardocosta.api.bank

import com.ricardocosta.api.bank.listeners.BlockHoundRegisterListener
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankAPIApplication

fun main(vararg args: String) {
    runApplication<BankAPIApplication>(*args) {
        addListeners(BlockHoundRegisterListener())
    }
}
