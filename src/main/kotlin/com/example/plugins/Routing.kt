package com.example.plugins

import com.example.repository.auth.AuthRepository
import com.example.route.authRouting
import com.example.route.openApiRoutes
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.apiRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    install(Routing){
        get("/openapi.json") {
            call.respond(application.openAPIGen.api.serialize())
        }
        get("/") {
            call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
        }
        apiRouting {
            val repository by inject<AuthRepository>()

            authRouting(repository)
            openApiRoutes(repository)
        }
    }
}
