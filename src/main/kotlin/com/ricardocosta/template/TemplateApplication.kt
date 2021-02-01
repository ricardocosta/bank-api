package com.ricardocosta.template

import com.ricardocosta.template.listeners.BlockHoundRegisterListener
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TemplateApplication

fun main(vararg args: String) {
    runApplication<TemplateApplication>(*args) {
        addListeners(BlockHoundRegisterListener())
    }
}
