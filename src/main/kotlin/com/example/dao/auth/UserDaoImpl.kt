package com.example.dao.auth

import com.mongodb.client.model.Filters
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserDaoImpl(
    database: CoroutineDatabase
) : UserDao {
    private val users = database.getCollection<UserEntity>()

    override suspend fun insertUser(userEntity: UserEntity): UserEntity {
        users.insertOne(userEntity)
        return userEntity
    }

    override suspend fun findUserByEmail(email: String): UserEntity? =
        users.find(Filters.eq("email", email)).first()

}