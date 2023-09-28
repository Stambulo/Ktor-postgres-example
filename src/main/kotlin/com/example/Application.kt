package com.example

import com.example.DatabaseFactory.dbQuery
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

// https://medium.com/@zaiddkhhan/postgres-implementation-in-ktor-backend-808fcb8244d8

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
    val dao : UserDao = UserDaoImpl()
    DatabaseFactory.init()
}

object DatabaseFactory {
    fun init() {
        val database =  Database.connect(
            url = "jdbc:postgresql://localhost:5432/ktor-db-example",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "Sa135244"
        )
        transaction(database) {
            SchemaUtils.create(Players)
        }
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val profilePic: String?
)

object Players : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128)
    val profilePic = varchar("profilePic", 1024).nullable()

    override val primaryKey = PrimaryKey(id)
}

interface UserDao {
    suspend fun allUsers(): List<Player>
    suspend fun user(id: Int): Player?
    suspend fun addNewUser(name: String, profilePic: String?): Player?
    suspend fun editUser(id: Int, name: String, profilePic: String?): Boolean
    suspend fun deleteUser(id: Int): Boolean
}

class UserDaoImpl : UserDao {

    override suspend fun allUsers(): List<Player> = dbQuery{
        Players.selectAll().map(::resultRowToArticle)
    }

    override suspend fun user(id: Int): Player?= dbQuery {
        Players.select{
            Players.id eq id
        }
            .map (::resultRowToArticle)
            .singleOrNull()
    }

    override suspend fun addNewUser(name: String, profilePic: String?): Player? = dbQuery{
        val insertStatement = Players.insert {
            it[Players.name] = name
            it[Players.profilePic] = profilePic
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun editUser(id: Int, name: String, profilePic: String?): Boolean = dbQuery {
        Players.update({ Players.id eq id }) {
            it[Players.name] = name
            it[Players.profilePic] = profilePic
        } > 0
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery{
        Players.deleteWhere { Players.id eq id } > 0
    }

    private fun resultRowToArticle(row : ResultRow) = Player(
        id = row[Players.id],
        name = row[Players.name],
        profilePic = row[Players.profilePic]
    )
}