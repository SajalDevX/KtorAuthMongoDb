package com.example.route

import com.example.model.AuthResponse
import com.example.plugins.RoleManagement
import com.example.repository.auth.AuthRepository
import com.example.util.JwtTokenBody
import com.example.util.Response
import com.example.util.authenticateWithJwt
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.ktor.server.plugins.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("RolesQueryLogger")

fun NormalOpenAPIRoute.openApiRoutes(repository: AuthRepository) {
    authenticateWithJwt(RoleManagement.SUPER_ADMIN.role) {
        route("roles").post<Unit, AuthResponse, RolesQuery, JwtTokenBody> { _, params ->
            try {
                val userId = principal().userId
                logger.info("Received request to change role with params: $params")
                when (val result = repository.changeUserRole(userId, params.requestId, params.role)) {
                    is Response.Success -> {
                        logger.info("Role updated successfully: ${result.data.data}")
                        respond(
                            AuthResponse(
                                data = result.data.data
                            )
                        )
                    }
                    is Response.Error -> {
                        logger.error("Failed to update user role: ${result.data.errorMessage}")
                        respond(
                            AuthResponse(
                                errorMessage = result.data.errorMessage ?: "Failed to update user role"
                            )
                        )
                    }
                }
            } catch (badRequestError: BadRequestException) {
                logger.error("Bad request error: ${badRequestError.message}")
                respond(
                    AuthResponse(
                        errorMessage = badRequestError.message ?: "Bad request"
                    )
                )
            } catch (anyError: Throwable) {
                logger.error("Unexpected error occurred: ${anyError.message}", anyError)
                respond(
                    AuthResponse(
                        errorMessage = "An unexpected error has occurred, try again!"
                    )
                )
            }
        }
    }
}

@Serializable
data class RolesQuery(
    val requestId: String,
    val role: String
)
