package com.example.route

import com.example.model.AuthResponse
import com.example.model.SignInParams
import com.example.model.SignUpParams
import com.example.repository.auth.AuthRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Routing.authRouting() {
    val repository by inject<AuthRepository>()

    route(path = "/signup") {
        post {
            val params = call.receiveNullable<SignUpParams>()
            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid Credentials!"
                    )
                )
                return@post
            }
            val result = repository.signUp(params)
            call.respond(
                status = result.code,
                message = result.data
            )
        }
    }
    route(path = "/login") {
        post {
            val params = call.receiveNullable<SignInParams>()
            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(
                        errorMessage = "Invalid Credentials!"
                    )
                )
                return@post
            }
            val result = repository.signIn(params)
            call.respond(
                status = result.code,
                message = result.data
            )
        }
    }
}