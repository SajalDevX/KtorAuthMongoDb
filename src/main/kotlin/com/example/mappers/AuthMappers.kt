package com.example.mappers

import com.example.dao.auth.UserEntity
import com.example.model.SignUpParams

fun SignUpParams.toUserEntity() =
    UserEntity(
        username = name,
        email = email,
        password = password
    )

