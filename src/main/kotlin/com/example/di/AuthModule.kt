package com.example.di

import com.example.dao.auth.UserDao
import com.example.dao.auth.UserDaoImpl
import com.example.repository.auth.AuthRepository
import com.example.repository.auth.AuthRepositoryImpl
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl(get()) }
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("auth_db")
    }
}