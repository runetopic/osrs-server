package com.osrs.network

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.game.world.World
import com.osrs.network.codec.CodecChannelHandler
import com.osrs.network.packet.Packet
import com.osrs.network.packet.builder.PacketBuilder
import io.ktor.network.sockets.ServerSocket
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Network @Inject constructor(
    private val server: ServerSocket,
    private val codecs: Set<CodecChannelHandler>,
    private val builders: Map<KClass<*>, PacketBuilder<Packet>>,
    private val world: World
) {
    private val logger = InlineLogger()

    private val scope = CoroutineScope(
        coroutineContext + SupervisorJob() + CoroutineExceptionHandler { ctx, throwable ->
            logger.error { "${throwable.message} $ctx" }
        }
    )

    fun bind() = runBlocking {
        logger.info { "Server is now accepting connections on ${server.localAddress} and listening for incoming connections." }
        with(scope) {
            while (this.isActive) {
                val session = Session(server.accept(), codecs, builders, world)
                launch(Dispatchers.IO) { session.connect() }
            }
        }
    }
}
