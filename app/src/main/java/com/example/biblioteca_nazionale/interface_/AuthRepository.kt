package com.example.biblioteca_nazionale.interface_


interface AuthRepository {

    suspend fun registerUser(email: String, password: String): Boolean

}