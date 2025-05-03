package com.andyc.checker.presentation.check_chat.mapper

import com.andyc.checker.domain.Message
import com.andyc.checker.domain.MessageRole
import com.andyc.checker.presentation.check_chat.model.MessageUi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Message.toMessageUi(): MessageUi {
    val lastMessageEpochSecond = sentEpochSecond!!

    val now = LocalDateTime.now()
    val lastMessageTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(lastMessageEpochSecond),
        ZoneId.systemDefault()
    )
    val lastMessageTimeString = if (now.year == lastMessageTime.year && now.dayOfYear == lastMessageTime.dayOfYear) {
        DateTimeFormatter.ofPattern("HH:mm")
            .format(lastMessageTime)
    } else {
        DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
            .format(lastMessageTime)
    }

    return MessageUi(
        id = id,
        byUser = role == MessageRole.USER,
        sentAt = lastMessageTimeString,
        content = content!!
    )
}