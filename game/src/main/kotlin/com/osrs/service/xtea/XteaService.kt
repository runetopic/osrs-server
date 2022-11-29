package com.osrs.service.xtea

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import com.mongodb.client.MongoClient
import org.litote.kmongo.getCollection

@Singleton
class XteaService @Inject constructor(
    mongoClient: MongoClient
) {
    private val xteas: Map<Int, Xtea> = mongoClient.getDatabase("api").getCollection<Xtea>("xteas209").find().associateBy { it.mapSquare }

    private val logger = InlineLogger()

    init {
        logger.info { "Finished loading ${xteas.size} xteas from the database." }
    }

    fun find(regionId: Int): List<Int> = xteas[regionId]?.key ?: throw IllegalStateException("Missing keys for $regionId")
}
