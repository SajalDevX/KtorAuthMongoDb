package com.example.repository.auth


import com.example.dao.auth.UserDao
import com.example.mappers.toUserEntity
import com.example.model.AuthResponse
import com.example.model.AuthResponseData
import com.example.model.SignInParams
import com.example.model.SignUpParams
import com.example.plugins.JwtController
import com.example.util.JwtTokenBody
import com.example.util.Response
import io.ktor.http.*

class AuthRepositoryImpl(
    private val userDao: UserDao
) : AuthRepository {
    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        return if (userAlreadyExists(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict, data = AuthResponse(
                    errorMessage = "A user with this email already exists"
                )
            )
        } else {
            val insertedUser = userDao.insertUser(params.toUserEntity().copy())
            if (insertedUser == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError, data = AuthResponse(
                        errorMessage = "Oops we could not register the user, Try later!"
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = insertedUser.id,
                            name = insertedUser.username!!,
                            token = JwtController.tokenProvider(JwtTokenBody(userType = insertedUser.userType, userId = insertedUser.id, email = insertedUser.email!!)),
                            email = insertedUser.email,
                            role = insertedUser.userType

                        )
                    )
                )
            }
        }
    }

    override suspend fun signIn(params: SignInParams): Response<AuthResponse> {
        val user = userDao.findUserByEmail(params.email)
        return if (user == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    errorMessage = "Invalid Credential, no user with this email"
                )
            )
        } else {
//            val hashedPassword = hashPassword(password = params.password)
            if (user.password == params.password) {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = user.id,
                            name = user.username!!,
                            token = JwtController.tokenProvider(JwtTokenBody(userType = user.userType, userId = user.id, email = user.email!!)),
                            email = user.email,
                            role = user.userType
                        )
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Forbidden,
                    data = AuthResponse(
                        errorMessage = "Invalid Credentials, wrong password"
                    )
                )
            }
        }
    }

    override suspend fun changeUserRole(currentUserId: String, requestId: String, role: String): Response<AuthResponse> {
        val requestedUser = userDao.findUserById(userId = requestId)
        val currentUser = userDao.findUserById(userId = currentUserId)
            ?: return Response.Error(
                code = HttpStatusCode.Forbidden,
                data = AuthResponse(
                    errorMessage = "Current user not found"
                )
            )

        return if (currentUser.userType == "super_admin") {
            if (requestedUser == null) {
                Response.Error(
                    code = HttpStatusCode.NotFound,
                    data = AuthResponse(
                        errorMessage = "Requested user not found"
                    )
                )
            } else {
                val updateSuccess = userDao.updateUserRole(requestId, role)
                if (updateSuccess) {
                    val username = requestedUser.username ?: "Unknown error in db"
                    val email = requestedUser.email ?: "Unknown error in db"
                    val token = JwtController.tokenProvider(JwtTokenBody(userType = requestedUser.userType, userId = requestedUser.id , email = email))
                    Response.Success(
                        data = AuthResponse(
                            data = AuthResponseData(
                                id = requestedUser.id,
                                name = username,
                                token = token,
                                email = email,
                                role = requestedUser.userType
                            )
                        )
                    )
                } else {
                    Response.Error(
                        code = HttpStatusCode.InternalServerError,
                        data = AuthResponse(
                            errorMessage = "Failed to update user role"
                        )
                    )
                }
            }
        } else {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = AuthResponse(
                    errorMessage = "You do not have permission to change user roles"
                )
            )
        }
    }




    private suspend fun userAlreadyExists(email: String): Boolean {
        return userDao.findUserByEmail(email) != null
    }
}