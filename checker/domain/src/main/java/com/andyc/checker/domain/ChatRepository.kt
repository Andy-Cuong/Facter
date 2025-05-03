package com.andyc.checker.domain

import com.andyc.core.domain.util.DataError
import com.andyc.core.domain.util.Result

interface ChatRepository {

    suspend fun getChatHistory(): Result<List<Chat>, DataError>

    suspend fun getChat(chatId: String): Result<Chat, DataError.Network>

    suspend fun upsertChat(chat: Chat): Result<String, DataError.Network>

    suspend fun deleteChat(chatId: String): Result<String, DataError.Network>
}