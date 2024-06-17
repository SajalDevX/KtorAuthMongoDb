package com.example.repository.auth


import com.example.dao.auth.UserDao
import com.example.mappers.toUserEntity
import com.example.model.AuthResponse
import com.example.model.AuthResponseData
import com.example.model.SignInParams
import com.example.model.SignUpParams
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
            val insertedUser = userDao.insertUser(params.toUserEntity().copy(

            ))
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
                            token = "generateToken(params.email)"
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
                            token = "generateToken(params.email)"
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

    private suspend fun userAlreadyExists(email: String): Boolean {
        return userDao.findUserByEmail(email) != null
    }
}