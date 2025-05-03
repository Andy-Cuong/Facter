package com.andyc.checker.domain

import com.andyc.core.domain.util.DataError
import com.andyc.core.domain.util.Result

/**
 * Interface that interacts with the language model
 */
interface CheckRepository {

    suspend fun generateChatTitle(chat: Chat): Result<String, DataError.Network>

    suspend fun chatCompletion(chat: Chat): Result<Message, DataError.Network>
}