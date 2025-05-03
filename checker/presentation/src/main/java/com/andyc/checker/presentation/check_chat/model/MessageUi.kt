package com.andyc.checker.presentation.check_chat.model

data class MessageUi(
    val id: String,
    val byUser: Boolean,
    val sentAt: String,
    val content: String
)
