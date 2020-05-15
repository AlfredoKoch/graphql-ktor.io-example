package com.example.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.KtorExperimentalAPI
import org.litote.kmongo.json

class BlockChainDownloader {

    @KtorExperimentalAPI
    suspend fun downloadBlocks(): String {
        val client = HttpClient(CIO)
        try {
            val response = client.get<HttpResponse>("https://eos.greymass.com/v1/chain/get_info")
            if (response.status == HttpStatusCode.OK) {
                val body = response.json
                return body
            }
        }catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }
        return ""
    }

}