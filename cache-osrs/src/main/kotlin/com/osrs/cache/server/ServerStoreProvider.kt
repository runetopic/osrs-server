package com.osrs.cache.server

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.google.inject.Provider
import com.osrs.config.ServerConfig
import io.netty.buffer.ByteBufAllocator
import java.io.File
import java.net.URL
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.zip.ZipInputStream
import kotlin.io.path.exists
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarBuilder
import me.tongfei.progressbar.ProgressBarStyle
import org.openrs2.cache.Store

class ServerStoreProvider @Inject constructor(
    val config: ServerConfig,
    private val alloc: ByteBufAllocator
) : Provider<Store> {

    private val serverCachePath = config.cache.server
    private val major = config.build.major

    private val logger = InlineLogger()

    private val json = Json { ignoreUnknownKeys = true }

    override fun get(): Store {
        downloadVanillaCache()
        return Store.open(serverCachePath, alloc)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun downloadVanillaCache() {
        val serverCacheFile = serverCachePath.toFile()

        if (serverCacheFile.exists() && serverCacheFile.walk().count() > 1) {
            return
        }

        val taskName = "Extracting files from archive $OPEN_RS2_ARCHIVE for build $major:"

        val pb = ProgressBarBuilder()
            .setInitialMax(DOWNLOAD_STEPS)
            .continuousUpdate()
            .setUnit(" steps", 1)
            .setTaskName(taskName)
            .setMaxRenderedLength(taskName.length + 85)
            .build()

        if (!serverCachePath.exists()) serverCacheFile.mkdirs()

        val caches = URL("${OPEN_RS2_ARCHIVE}/caches.json").openStream()

        val openRS2Cache = json.decodeFromStream<List<OpenRS2Caches>>(caches)
            .last { cache -> cache.builds.isNotEmpty() && cache.builds.any { it.major == major } }

        caches.close()

        val zipped = URL("${OPEN_RS2_RUNESCAPE_CACHES}/${openRS2Cache.id}/disk.zip").openStream()

        pb.stepBy(2)

        ZipInputStream(zipped).use { zis ->
            while (true) {
                val zipEntry = zis.nextEntry ?: break

                if (zipEntry.isDirectory) continue

                pb.step()

                val fileName = "${serverCachePath.toFile().path}/${zipEntry.name.replace(OPEN_RS2_ZIP_FOLDER_NAME, "")}"
                val file = File(fileName)
                val bytes = zis.readAllBytes()
                file.writeBytes(bytes)
            }
        }

        zipped.close()
        pb.close()

        logger.info { "Finished downloading cache files located in $serverCachePath" }
    }

    private companion object {
        const val OPEN_RS2_ARCHIVE = "https://archive.openrs2.org"
        const val OPEN_RS2_RUNESCAPE_CACHES = "${OPEN_RS2_ARCHIVE}/caches/runescape"
        const val OPEN_RS2_ZIP_FOLDER_NAME = "cache/"
        const val DOWNLOAD_STEPS: Long = 25 // This includes the file count + two API calls.
    }
}
