package com.osrs.network

object SessionResponseOpcode {
    const val HANDSHAKE_SUCCESS_OPCODE = 0
    const val LOGIN_SUCCESS_OPCODE = 2
    const val INVALID_USERNAME_PASSWORD_OPCODE = 3
    const val CLIENT_OUTDATED_OPCODE = 6
    const val BAD_SESSION_OPCODE = 10
}
