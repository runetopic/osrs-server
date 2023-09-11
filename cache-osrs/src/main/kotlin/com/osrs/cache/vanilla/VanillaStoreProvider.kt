package com.osrs.cache.vanilla

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Provider
import com.osrs.config.ServerConfig
import io.netty.buffer.ByteBufAllocator
import java.io.File
import java.net.URL
import java.util.zip.ZipInputStream
import kotlin.io.path.exists
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.tongfei.progressbar.ProgressBar
import org.openrs2.cache.Store

class VanillaStoreProvider @Inject constructor(
    val config: ServerConfig,
    private val alloc: ByteBufAllocator
) : Provider<Store> {

    private val vanillaCachePath = config.cache.vanilla
    private val major = config.build.major

    private val logger = InlineLogger()

    private val json = Json { ignoreUnknownKeys = true }

    override fun get(): Store {
        downloadVanillaCache()
        return Store.open(vanillaCachePath, alloc)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun downloadVanillaCache() {
        val vanillaCacheFile = vanillaCachePath.toFile()

        if (vanillaCacheFile.exists() && vanillaCacheFile.walk().count() > 1) {
            return
        }

        val pb = ProgressBar("Downloading cache for build $major : ", 25)

        if (!vanillaCachePath.exists()) vanillaCacheFile.mkdirs()

        val caches = URL("${OPEN_RS2_ARCHIVE}/caches.json").openStream()

        val openRS2Cache = json.decodeFromStream<List<OpenRS2Caches>>(caches)
            .last { cache -> cache.builds.isNotEmpty() && cache.builds.any { it.major == major } }

        caches.close()

        pb.step()

        val zipped = URL("${OPEN_RS2_RUNESCAPE_CACHES}/${openRS2Cache.id}/disk.zip").openStream()

        pb.step()

        ZipInputStream(zipped).use { zis ->
            while (true) {
                val zipEntry = zis.nextEntry ?: break

                if (zipEntry.isDirectory) continue

                val fileName = "${vanillaCachePath.toFile().path}/${zipEntry.name.replace("cache/", "")}"
                val file = File(fileName)
                val bytes = zis.readAllBytes()
                file.writeBytes(bytes)
                pb.step()
            }
        }

        zipped.close()
        pb.close()

        logger.info { "Finished downloading cache files from archive." }
    }

    private companion object {
        const val OPEN_RS2_ARCHIVE = "https://archive.openrs2.org"
        const val OPEN_RS2_RUNESCAPE_CACHES = "${OPEN_RS2_ARCHIVE}/caches/runescape"
    }
}
