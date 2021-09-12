package fr.spacefox.calvinandbot.discord.events

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MessageCreateListener(private var messageListener: MessageListener) : EventListener<MessageCreateEvent> {

    override fun getEventType(): Class<MessageCreateEvent> {
        return MessageCreateEvent::class.java
    }

    override fun listen(events: Flow<MessageCreateEvent>): Flow<Message> =
        messageListener.listen(events.map { it.message })
}
