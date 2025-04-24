package ru.mishgan325.chatappsocket.data.mappers

import ru.mishgan325.chatappsocket.data.api.model.Content
import ru.mishgan325.chatappsocket.data.websocket.model.ChatMessageWs
import ru.mishgan325.chatappsocket.dto.ChatMessageDto
import ru.mishgan325.chatappsocket.dto.SenderDto
import ru.mishgan325.chatappsocket.domain.models.Message
import ru.mishgan325.chatappsocket.domain.models.Sender

fun Content.toChatMessageDto(): ChatMessageDto {
    return ChatMessageDto(
        id = id,
        sender = SenderDto(sender.id, sender.username),
        content = content,
        fileUrl = fileUrl,
        timestamp = timestamp
    )
}

fun ChatMessageWs.toMessage(currentUsername: String): Message {
    return Message(
        id = chatMessageId,
        sender = Sender(
            username = sender,
            id = -1
        ),
        content = content,
        fileUrl = fileUrl,
        timestamp = timestamp,
        isMine = sender == currentUsername
    )
}

fun ChatMessageDto.toMessage(currentUserId: Long?): Message {
    return Message(
        id = id,
        sender = Sender(id = sender.id, username = sender.username),
        content = content,
        fileUrl = fileUrl ?: "",
        timestamp = timestamp,
        isMine = currentUserId == sender.id
    )
}