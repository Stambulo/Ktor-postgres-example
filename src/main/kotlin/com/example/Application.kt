package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.cio.*
import io.ktor.server.engine.*

// https://medium.com/@zaiddkhhan/postgres-implementation-in-ktor-backend-808fcb8244d8

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0"){
        configureSerialization()
        configureRouting()
        DatabaseFactory.init()
    }.start(wait = true)
}
