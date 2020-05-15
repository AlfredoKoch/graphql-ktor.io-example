package com.example.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.util.KtorExperimentalAPI
import java.nio.charset.Charset

class BlockChainDownloader {

    @KtorExperimentalAPI
    suspend fun downloadBlocks(): String {
        val client = HttpClient(CIO)
        try {
            val response = client.get<HttpResponse>("https://eos.greymass.com/v1/chain/get_info")
            if (response.status == HttpStatusCode.OK) {
                val body = response.readText(Charset.defaultCharset())
                val jsonBody = JsonParser.parseString(body).asJsonObject
                val blockId = jsonBody.get("head_block_id").asString
                if (blockId != null) {
                    saveBlock(client, blockId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }
        return ""
    }

    suspend fun saveBlock(client: HttpClient, blockId: String) {
        val jsonRequestBody = JsonObject()
        jsonRequestBody.addProperty("block_num_or_id", blockId)
        val blockResponse = client.post<HttpResponse> {
            url("https://eos.greymass.com/v1/chain/get_block")
            body = TextContent(jsonRequestBody.toString(), ContentType.Application.Json)
        }
        if (blockResponse.status == HttpStatusCode.OK) {
            val body = blockResponse.readText(Charset.defaultCharset())
            try {
                val jsonBody = JsonParser.parseString(body).asJsonObject
                Database.insertBlock(body, jsonBody.get("block_num").asString)
                print("Block inserted ${jsonBody.get("block_num")}")
                jsonBody.get("previous").asString?.let {
                    saveBlock(client, it)
                }
            }catch (e: java.lang.Exception) {
                print(body)
                e.printStackTrace()
            }
        } else {
            print("Error downloading block: ${blockResponse.status}")
            saveBlock(client, blockId)
        }
    }

}