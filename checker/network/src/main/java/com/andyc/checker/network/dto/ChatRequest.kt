package com.andyc.checker.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val messages: List<RequestMessage>,
    @SerialName("reasoning_effort") val reasoningEffort: String, // "low" or "high"
    val model: String,
) {
    @Serializable
    data class RequestMessage(
        val role: String,
        val content: String
    )
}