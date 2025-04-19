package ru.mishgan325.chatappsocket.data.mappers

import ru.mishgan325.chatappsocket.data.api.model.Content
import ru.mishgan325.chatappsocket.dto.ChatMessageDto
import ru.mishgan325.chatappsocket.dto.SenderDto
import ru.mishgan325.chatappsocket.models.Message
import ru.mishgan325.chatappsocket.models.Sender

fun Content.toChatMessageDto(): ChatMessageDto {
    return ChatMessageDto(
        id = id,
        sender = SenderDto(sender.id, sender.username),
        content = content,
        fileUrl = fileUrl,
        timestamp = timestamp
    )
}

fun ChatMessageDto.toMessage(userId: Long?): Message {
    return Message(
        id = id,
        sender = Sender(sender.id, sender.username),
        content = content,
        fileUrl = fileUrl ?: "",
        timestamp = timestamp,
        isMine = userId == sender.id
    )
}