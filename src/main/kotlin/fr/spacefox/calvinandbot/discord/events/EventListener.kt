package fr.spacefox.calvinandbot.discord.events

import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.Event
import fr.spacefox.calvinandbot.util.LoggerDelegate
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono

interface EventListener<T : Event> {

    companion object {
        val logger by LoggerDelegate()
    }

    fun getEventType(): Class<T>
//    fun listen(events: Flow<T>): Flow<Command>

    fun handleError(error: Throwable): Mono<Void> {
        logger.error("Unable to process ${getEventType().simpleName}", error)
        return Mono.empty()
    }

    fun listen(events: Flow<T>): Flow<Message>
}
