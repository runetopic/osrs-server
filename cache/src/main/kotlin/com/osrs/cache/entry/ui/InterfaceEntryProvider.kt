package com.osrs.cache.entry.ui

import com.github.michaelbull.logging.InlineLogger
import com.google.inject.Inject
import com.osrs.api.buffer.RSByteBuffer
import com.osrs.api.util.childId
import com.osrs.api.util.interfaceId
import com.osrs.api.util.packInterface
import com.osrs.cache.Cache
import com.osrs.cache.CacheModule.INTERFACE_INDEX
import com.osrs.cache.entry.EntryTypeProvider
import com.runetopic.cache.codec.decompress

class InterfaceEntryProvider @Inject constructor(
    private val cache: Cache,
) : EntryTypeProvider<InterfaceEntryType>() {
    private val logger = InlineLogger()

    override fun loadTypeMap(): Map<Int, InterfaceEntryType> = cache
        .index(INTERFACE_INDEX)
        .groups()
        .flatMap { group ->
            group.files().map {
                RSByteBuffer(it.data)
                    .loadEntryType(
                        InterfaceEntryType(
                            group.id.packInterface(it.id),
                            isIf3 = it.data[0].toInt() == -1
                        )
                    )
            }
        }
        .associateBy(InterfaceEntryType::id).also {
            logger.info { "Loaded ${it.entries.filter { it.value.isIf3 }.size} if3." }
            logger.info { "Loaded ${it.entries.filter { !it.value.isIf3 }.size} if1." }
        }

    private fun RSByteBuffer.loadEntryType(type: InterfaceEntryType): InterfaceEntryType = type.apply {
        if (isIf3) decodeIf3(this) else decodeIf1(this)
    }

    private fun RSByteBuffer.decodeIf3(type: InterfaceEntryType) {
        discard(1) // Unused.
        type.type = readUByte()
        type.contentType = readUShort()
        type.rawX = readShort()
        type.rawY = readShort()
        type.rawWidth = readUShort()
        type.rawHeight = if (type.type == 9) readShort() else readUShort()
        type.widthAlignment = readByte()
        type.heightAlignment = readByte()
        type.xAlignment = readByte()
        type.yAlignment = readByte()
        type.parentId = readUShort().let { if (it == 0xffff) -1 else it + type.id and 0xffff.inv() }
        type.isHidden = readUByte() == 1

        if (type.id.interfaceId() == 85) {
            logger.info { "Decoding ${type.id.interfaceId()}" }
            logger.info { "Type ${type.type}" }
            logger.info { "Content Type ${type.contentType}" }
            logger.info { "Raw height ${type.rawWidth}" }
            logger.info { "Raw width ${type.rawHeight}" }
        }

        when (type.type) {
            0 -> {
                type.scrollWidth = readUShort()
                type.scrollHeight = readUShort()
                type.noClickThrough = readUByte() == 1
            }

            3 -> {
                type.color = readInt()
                type.fill = readUByte() == 1
                type.transparencyTop = readUByte()
            }

            4 -> {
                type.fontId = readUShort().let { if (it == 0xffff) -1 else it }
                type.text = readStringCp1252NullTerminated()
                type.textLineHeight = readUByte()
                type.textXAlignment = readUByte()
                type.textYAlignment = readUByte()
                type.textShadowed = readUByte() == 1
                type.color = readInt()
            }

            5 -> {
                type.spriteId2 = readInt()
                type.spriteAngle = readUShort()
                type.spriteTiling = readUByte() == 1
                type.transparencyTop = readUByte()
                type.outline = readUByte()
                type.spriteShadow = readInt()
                type.spriteFlipV = readUByte() == 1
                type.spriteFlipH = readUByte() == 1
            }

            6 -> {
                type.modelType = 1
                type.modelId = readUShort().let { if (it == 0xffff) -1 else it }
                type.modelOffsetX = readShort()
                type.modelOffsetY = readShort()
                type.modelAngleX = readUShort()
                type.modelAngleY = readUShort()
                type.modelAngleZ = readUShort()
                type.modelZoom = readUShort()
                type.sequenceId = readUShort().let { if (it == 0xffff) -1 else it }
                type.modelOrthog = readUByte() == 1
                discard(2) // Unused.
                if (type.widthAlignment != 0) type.field3280 = readUShort()
                if (type.heightAlignment != 0) discard(2) // Unused.
            }

            9 -> {
                type.lineWid = readUByte()
                type.color = readInt()
                type.field3359 = readUByte() == 1
            }
        }
        type.flags = readMedium()
        type.dataText = readStringCp1252NullTerminated()

        type.actions = buildList {
            repeat(readUByte()) {
                add(readStringCp1252NullTerminated())
            }
        }

        type.dragZoneSize = readUByte()
        type.dragThreshold = readUByte()
        type.isScrollBar = readUByte() == 1
        type.spellActionName = readStringCp1252NullTerminated()
        discard(remaining())
    }

    private fun RSByteBuffer.decodeIf1(type: InterfaceEntryType) {
        type.type = readUByte()
        type.buttonType = readUByte()
        type.contentType = readUShort()
        type.rawX = readShort()
        type.rawY = readShort()
        type.rawWidth = readUShort()
        type.rawHeight = readUShort()
        type.transparencyTop = readUByte()
        type.parentId = readUShort().let { if (it == 0xffff) -1 else it + type.id and 0xffff.inv() }
        type.mouseOverRedirect = readUShort().let { if (it == 0xffff) -1 else it }
        if (type.id.interfaceId() == 85) {
            logger.info { "Decoding ${type.id.interfaceId()}" }
            logger.info { "Type ${type.type}" }
            logger.info { "Content Type ${type.contentType}" }
            logger.info { "Raw height ${type.rawWidth}" }
            logger.info { "Raw width ${type.rawHeight}" }
        }

        repeat(readUByte()) { // cs1 comparisons/values.
            discard(3) // Discard the cs1 comparisons and values.
        }

        repeat(readUByte()) { // cs1 instructions.
            repeat(readUShort()) {
                discard(2) // Discard the cs1 instructions values.
            }
        }

        when (type.type) {
            0 -> {
                type.scrollHeight = readUShort()
                type.isHidden = readUByte() == 1
            }

            1 -> discard(3) // Unused.
            2 -> {
                type.itemIds = List(type.rawWidth * type.rawHeight) { 0 }
                type.itemQuantities = List(type.rawWidth * type.rawHeight) { 0 }
                discard(4) // Discard flags.
                type.paddingX = readUByte()
                type.paddingY = readUByte()

                repeat(20) {
                    if (readUByte() == 1) {
                        discard(8) // Discard inventory sprites and offsets.
                    }
                }

                repeat(5) {
                    readStringCp1252NullTerminated() // Discard item actions.
                }

                type.spellActionName = readStringCp1252NullTerminated()
                type.spellName = readStringCp1252NullTerminated()
                discard(2) // Discard flags.
            }

            3 -> type.fill = readUByte() == 1
            5 -> {
                type.spriteId2 = readInt()
                type.spriteId = readInt()
            }

            6 -> {
                type.modelType = 1
                type.modelId = readUShort().let { if (it == 0xffff) -1 else it }

                type.modelType2 = 1
                type.modelId2 = readUShort().let { if (it == 0xffff) -1 else it }

                type.sequenceId = readUShort().let { if (it == 0xffff) -1 else it }
                type.sequenceId2 = readUShort().let { if (it == 0xffff) -1 else it }
                type.modelZoom = readUShort()
                type.modelAngleX = readUShort()
                type.modelAngleY = readUShort()
            }

            7 -> {
                type.itemIds = List(type.rawWidth * type.rawHeight) { 0 }
                type.itemQuantities = List(type.rawWidth * type.rawHeight) { 0 }
                type.textXAlignment = readUByte()
                type.fontId = readUShort().let { if (it == 0xffff) -1 else it }
                type.textShadowed = readUByte() == 1
                type.color = readInt()
                type.paddingX = readShort()
                type.paddingY = readShort()
                discard(1) // Discard flags.

                repeat(5) {
                    readStringCp1252NullTerminated() // Discard item actions.
                }
            }

            8 -> type.text = readStringCp1252NullTerminated()
        }

        if (type.type == 1 || type.type == 3 || type.type == 4) {
            if (type.type != 3) {
                type.textXAlignment = readUByte()
                type.textYAlignment = readUByte()
                type.textLineHeight = readUByte()
                type.fontId = readUShort().let { if (it == 0xffff) -1 else it }
                type.textShadowed = readUByte() == 1
                if (type.type == 4) {
                    type.text = readStringCp1252NullTerminated()
                    type.text2 = readStringCp1252NullTerminated()
                }
            }
            type.color = readInt()
            if (type.type != 1) {
                type.color2 = readInt()
                type.mouseOverColor = readInt()
                type.mouseOverColor2 = readInt()
            }
        }

        when (type.buttonType) {
            2 -> {
                type.spellActionName = readStringCp1252NullTerminated()
                type.spellName = readStringCp1252NullTerminated()
                discard(2) // Discard flags.
            }

            1, 4, 5, 6 -> {
                type.buttonText = readStringCp1252NullTerminated()
                if (type.buttonText.isEmpty()) {
                    type.buttonText = when (type.buttonType) {
                        1 -> "Ok"
                        4, 5 -> "Select"
                        6 -> "Continue"
                        else -> throw IllegalArgumentException("Could not set text for button type ${type.buttonType}.")
                    }
                }
            }
        }
    }
}
