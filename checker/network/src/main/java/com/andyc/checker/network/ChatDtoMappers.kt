package com.andyc.checker.network

import com.andyc.checker.domain.Chat
import com.andyc.checker.domain.Message
import com.andyc.checker.domain.MessageRole
import com.andyc.checker.network.dto.ChatRequest
import com.andyc.checker.network.dto.ChatResponse

fun Chat.toChatRequest(): ChatRequest {
    val messages = messages.map { message ->
        ChatRequest.RequestMessage(
            role = when (message.role) {
                MessageRole.SYSTEM -> "system"
                MessageRole.USER -> "user"
                MessageRole.ASSISTANT -> "assistant"
                null -> "assistant"
            },
            content = message.content ?: ""
        )
    }

    return ChatRequest(
        messages = messages,
        reasoningEffort = "low",
        model = "grok-3-mini-latest"
    )
}

fun ChatResponse.toMessage(): Message {
    return Message(
        id = id,
        role = when (choices[0].message.role) {
            "system" -> MessageRole.SYSTEM
            "user" -> MessageRole.USER
            else -> MessageRole.ASSISTANT
        },
        sentEpochSecond = created,
        content = choices[0].message.run { refusal ?: content }
    )
}