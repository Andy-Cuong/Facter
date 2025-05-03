package com.andyc.checker.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val id: String,
    @SerialName("object") val obj: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>
) {
    @Serializable
    data class Choice(
        val index: Int,
        val message: ResponseMessage,
        val finishReason: String? = null
    ) {
        @Serializable
        data class ResponseMessage(
            val role: String,
            val content: String,
            @SerialName("reasoning_content") val reasoningContent: String,
            val refusal: String?
        )
    }
}

