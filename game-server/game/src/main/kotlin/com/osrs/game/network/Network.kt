package com.osrs.game.network

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.game.network.codec.CodecChannelHandler
import com.osrs.game.network.packet.Packet
import com.osrs.game.network.packet.builder.PacketBuilder
import com.osrs.game.world.World
import io.ktor.http.parseServerSetCookieHeader
import io.ktor.network.sockets.ServerSocket
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

class Network @Inject constructor(
    private val server: ServerSocket,
    private val world: World,
    private val codecs: Set<CodecChannelHandler>,
    private val builders: Map<KClass<*>, PacketBuilder<Packet>>
) {
    private val logger = InlineLogger()

    private val scope = CoroutineScope(
        coroutineContext + SupervisorJob() + CoroutineExceptionHandler { ctx, throwable ->
            logger.error { "${throwable.message} $ctx" }
        }
    )

    fun bind(launchTimeInMS: Long) = runBlocking {
        logger.info { "Server is now accepting connections on ${server.localAddress} and listening for incoming connections. Server took ${launchTimeInMS}ms to launch." }
        world.isOnline = true
        with(scope) {
            while (this.isActive) {
                val session = Session(world, server.accept(), codecs, builders)
                launch(Dispatchers.IO) { session.connect() }
            }
        }
    }

    fun shutdown() {
        world.isOnline = false
        server.close()
        logger.info { "Network has been shutdown." }
    }
}
