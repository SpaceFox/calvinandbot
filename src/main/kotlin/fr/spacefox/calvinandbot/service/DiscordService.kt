package fr.spacefox.calvinandbot.service

import discord4j.core.DiscordClient
import discord4j.core.event.domain.Event
import fr.spacefox.calvinandbot.discord.events.MessageCreateListener
import fr.spacefox.calvinandbot.discord.events.MessageUpdateListener
import fr.spacefox.calvinandbot.util.LoggerDelegate
import fr.spacefox.calvinandbot.util.Properties
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger

class DiscordService : KoinComponent {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    private val createListener by inject<MessageCreateListener>()
    private val updateListener by inject<MessageUpdateListener>()

    suspend fun init() {
        log.info("Initializing Discord Service")
        val token = Properties.value("discord4j.token", "/calvin")

        val listeners = listOf(createListener, updateListener)

        DiscordClient.create(token).withGateway { client ->
            mono {
                listeners.forEach { listener ->
                    client.on(listener.getEventType()).asFlow()
                        // messageFlow is a Flow<Event>, the fake cast to Flow<Nothing> is here only to allow
                        // compilation due to type erasure
                        .let { messageFlow: Flow<Event> -> listener.listen(messageFlow as Flow<Nothing>) }
                        .catch { log.error(it.message, it) }
                        .onEach { log.info(it.toString()) }
                        .launchIn(this)
                }
            }
        }.awaitSingle()
    }
}
