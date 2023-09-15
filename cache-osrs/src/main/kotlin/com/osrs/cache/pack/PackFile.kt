package com.osrs.cache.pack

import io.netty.buffer.ByteBuf
import java.io.PrintWriter
import org.openrs2.cache.Cache

abstract class PackFile(
    open val cache: Cache,
    val fileName: String,
    val archive: Int,
    val group: Int,
) {
    val capacity: Int
        get() = cache.capacity(archive, group)

    abstract fun unpack()
    abstract fun PrintWriter.printCode(buf: ByteBuf, code: Int)
}
