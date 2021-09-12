package fr.spacefox.calvinandbot.discord.events

import discord4j.core.event.domain.message.MessageUpdateEvent
import discord4j.core.`object`.entity.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle

class MessageUpdateListener(val messageListener: MessageListener) : EventListener<MessageUpdateEvent> {

    override fun getEventType(): Class<MessageUpdateEvent> {
        return MessageUpdateEvent::class.java
    }

    override fun listen(events: Flow<MessageUpdateEvent>): Flow<Message> =
        messageListener.listen(
            events
                .filter { it.isContentChanged }
                .map { it.message.awaitSingle() }
        )
}
