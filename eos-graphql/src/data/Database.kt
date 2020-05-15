package com.example.data

import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.litote.kmongo.util.KMongoUtil
import java.lang.Exception

object Database {

    const val localDB = "mongodb://localhost:27017"
    const val remoteDB = "mongodb://192.168.1.5:27017"

    private val mongo = KMongo.createClient(localDB)
    private val eosDatabase = mongo.getDatabase("eos")
    fun getBlockById(id: String): String? {
        return eosDatabase.getCollection("block").findOne(
            "{id: \"${id}\"}"
        )?.toJson()
    }

    fun getLatestBlock(): String? {
        return eosDatabase.getCollection("block").find().sort(
            KMongoUtil.toBson("{block_num: 1}")
        ).limit(1).first()?.toJson()
    }

    fun insertBlock(info: String, blockNum: String) {
        val collection = eosDatabase.getCollection("block")
        try {
            if (collection.findOne("{block_num: $blockNum}").isNullOrEmpty()) {
                collection.insertOne(Document.parse(info))
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }
}