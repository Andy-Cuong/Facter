package com.andyc.checker.network

import android.util.Log
import com.andyc.checker.domain.Chat
import com.andyc.checker.domain.CheckRepository
import com.andyc.checker.domain.Message
import com.andyc.checker.domain.MessageRole
import com.andyc.checker.network.dto.ChatRequest
import com.andyc.checker.network.dto.ChatResponse
import com.andyc.core.data.networking.post
import com.andyc.core.domain.util.DataError
import com.andyc.core.domain.util.Result
import com.andyc.core.domain.util.map
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

class XaiCheckRepository(
    private val httpClient: HttpClient
): CheckRepository {

    override suspend fun generateChatTitle(chat: Chat): Result<String, DataError.Network> {
        val firstUserMessage = chat.messages.first { it.role == MessageRole.USER }

        val titleRequest = ChatRequest(
            messages = listOf(
                ChatRequest.RequestMessage(
                    role = "system",
                    content = "Be as concise as possible, and make the response suitable to be a title"
                ),
                ChatRequest.RequestMessage(
                    role = "user",
                    content = "Summarize the following message within 5 words: ${firstUserMessage.content}"
                )
            ),
            reasoningEffort = "low",
            model = "grok-3-mini-latest"
        )

        return httpClient.post<ChatRequest, ChatResponse>(
            route = "v1/chat/completions",
            body = titleRequest
        ).map { chatResponse ->
            chatResponse.choices[0].message.run { refusal ?: content }
        }
    }

    override suspend fun chatCompletion(chat: Chat): Result<Message, DataError.Network> {
        val systemMessage = Message(
            role = MessageRole.SYSTEM,
            content = "You are Facter, an assistant that helps specifically with questions that involve fact-checking. " +
                    "If the question isn't related to fact-checking, or is heavily biased or opinionated, " +
                    "or contains information that cannot be verified, simply deny the request. Try to be concise, " +
                    "but do not leave out important details if any. Do not use Markdown or any formatting. " +
                    "Always cite the source of information in the form \"Source: <URL>\" (without quotes)"
        )

        val fullChat = chat.copy(
            messages = chat.messages + systemMessage
        )

        return httpClient.post<ChatRequest, ChatResponse>(
            route = "/v1/chat/completions",
            body = fullChat.toChatRequest()
        ).map { chatResponse ->
            chatResponse.toMessage()
        }
    }
}