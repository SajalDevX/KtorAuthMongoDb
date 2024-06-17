package com.example

import com.example.di.configureDI
import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
