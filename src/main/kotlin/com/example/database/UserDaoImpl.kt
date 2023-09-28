package com.example.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserDaoImpl : UserDao {

    override suspend fun allUsers(): List<Player> = DatabaseFactory.dbQuery {
        Players.selectAll().map(::resultRowToArticle)
    }

    override suspend fun user(id: Int): Player?= DatabaseFactory.dbQuery {
        Players.select {
            Players.id eq id
        }
            .map(::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun addNewUser(name: String, profilePic: String?): Player? = DatabaseFactory.dbQuery {
        val insertStatement = Players.insert {
            it[Players.name] = name
            it[Players.profilePic] = profilePic
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun editUser(id: Int, name: String, profilePic: String?): Boolean = DatabaseFactory.dbQuery {
        Players.update({ Players.id eq id }) {
            it[Players.name] = name
            it[Players.profilePic] = profilePic
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = DatabaseFactory.dbQuery {
        Players.deleteWhere { Players.id eq id } > 0
    }

    private fun resultRowToArticle(row : ResultRow) = Player(
        id = row[Players.id],
        name = row[Players.name],
        profilePic = row[Players.profilePic]
    )
}