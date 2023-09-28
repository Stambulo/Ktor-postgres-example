package com.example.database

interface UserDao {
    suspend fun allUsers(): List<Player>
    suspend fun user(id: Int): Player?
    suspend fun addNewUser(name: String, profilePic: String?): Player?
    suspend fun editUser(id: Int, name: String, profilePic: String?): Boolean
    suspend fun deleteUser(id: Int): Boolean
}
