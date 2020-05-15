package com.example.data

import org.bson.Document
import org.litote.kmongo.KMongo
import org.litote.kmongo.findOne
import org.litote.kmongo.util.KMongoUtil

object Database {
    private val mongo = KMongo.createClient("mongodb://localhost:27017")
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

    fun insertBlock(info: String) {
        eosDatabase.getCollection("block").insertOne(Document.parse(info))
    }
}