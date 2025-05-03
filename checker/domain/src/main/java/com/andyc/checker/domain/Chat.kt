package com.andyc.checker.domain

import java.util.UUID

data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val userId: String? = null,
    val title: String = "",
    val messages: List<Message> = emptyList()
)
