package com.example.plugins

import com.example.database.UserDao
import com.example.database.UserDaoImpl
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting() {

    val dao : UserDao = UserDaoImpl()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/adduser") {
            val formParameters = call.receiveParameters()
            val name = formParameters.getOrFail("name")
            val profilePic = formParameters.getOrFail("profilePic")
            val article = dao.addNewUser(name, profilePic)
            call.respond("User added successfully")
        }

        get("/users") {
            val listOfUsers = dao.allUsers()
            if(listOfUsers.isEmpty()){
                call.respond("emptyyyy")
            }else{
                call.respond(listOfUsers.toString())
            }
        }
    }
}
