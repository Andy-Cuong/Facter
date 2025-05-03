package com.andyc.checker.domain

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole? = null,
    val sentEpochSecond: Long? = null,
    val content: String? = null
)

enum class MessageRole {
    SYSTEM,
    USER,
    ASSISTANT
}
