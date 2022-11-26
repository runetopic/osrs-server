package com.osrs.network

object SessionRequestOpcode {
    const val JS5_HIGH_PRIORITY_OPCODE = 0
    const val JS5_LOW_PRIORITY_OPCODE = 1
    const val JS5_LOGGED_IN_OPCODE = 2
    const val JS5_SWITCH_OPCODE = 3
    const val JS5_ENCRYPTION_OPCODE = 4
    const val HANDSHAKE_LOGIN_OPCODE = 14
    const val HANDSHAKE_JS5_OPCODE = 15
    const val LOGIN_NORMAL_OPCODE = 16
}
