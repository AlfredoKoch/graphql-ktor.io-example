package com.example

import com.example.data.BlockChainDownloader
import com.example.data.Database
import graphql.schema.GraphQLSchema
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import ktor.graphql.config
import ktor.graphql.graphQL

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    routing {
        get("/chain") {
            val text = BlockChainDownloader().downloadBlocks()
            call.respondText(if (text.isEmpty()) "internal error" else text)
        }
        get("/") {
            call.respondText(Database.getBlockById("0704b90f303224fcba060518d67a2eaec599590cf71750b36c0d2864743f531b") ?: "{error: \"not found\"}", contentType = ContentType.Application.Json)
        }
        graphQL(path= "/graphql", schema = graphQLSchema) {
            config {
                graphiql = true
            }
        }
    }
}

