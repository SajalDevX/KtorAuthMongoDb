package com.example.dao.auth

import com.mongodb.client.model.Filters
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.setValue
import org.slf4j.LoggerFactory

class UserDaoImpl(
    database: CoroutineDatabase
) : UserDao {
    private val users = database.getCollection<UserEntity>()
    private val logger = LoggerFactory.getLogger(UserDaoImpl::class.java)

    override suspend fun insertUser(userEntity: UserEntity): UserEntity {
        users.insertOne(userEntity)
        return userEntity
    }

    override suspend fun findUserByEmail(email: String): UserEntity? =
        users.find(Filters.eq("email", email)).first()

    override suspend fun updateUserRole(userId: String, role: String): Boolean {
        val updateResult = users.updateOne(Filters.eq("_id", userId), setValue(UserEntity::userType, role))
        logger.debug("Update result: matchedCount=${updateResult.matchedCount}, modifiedCount=${updateResult.modifiedCount}")
        return updateResult.matchedCount > 0
    }

    override suspend fun findUserById(userId: String): UserEntity? =
        users.find(Filters.eq("_id", userId)).first()

}
