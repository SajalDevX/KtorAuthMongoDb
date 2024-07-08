package com.example.dao.auth

interface UserDao{

    suspend fun insertUser(userEntity: UserEntity):UserEntity?
    suspend fun findUserByEmail(email:String):UserEntity?
    suspend fun updateUserRole(userId:String,role:String):Boolean
    suspend fun findUserById(userId:String):UserEntity?
}