package com.andyc.checker.presentation.list.mapper

import com.andyc.checker.domain.Chat
import com.andyc.checker.presentation.list.model.ChatUi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Chat.toChatUi(): ChatUi {
    val lastMessageEpochSecond = messages.last().sentEpochSecond!!

    val now = LocalDateTime.now()
    val lastMessageTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(lastMessageEpochSecond),
        ZoneId.systemDefault()
    )
    val lastMessageTimeString = if (now.year == lastMessageTime.year && now.dayOfYear == lastMessageTime.dayOfYear) {
        DateTimeFormatter.ofPattern("HH:mm")
            .format(lastMessageTime)
    } else {
        DateTimeFormatter.ofPattern("EEE dd/MM/yyyy")
            .format(lastMessageTime)
    }

    return ChatUi(
        id = id,
        userId = userId!!,
        title = title,
        lastMessageTime = lastMessageTimeString,
        lastMessage = messages.last().content!!
    )
}