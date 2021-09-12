package fr.spacefox.calvinandbot.discord.commands

import discord4j.core.`object`.entity.Message
import fr.spacefox.calvinandbot.service.LuceneReadService
import fr.spacefox.calvinandbot.util.LoggerDelegate
import org.slf4j.Logger
import reactor.core.publisher.Mono

class SearchCommand(override val message: Message, val query: String, val luceneReadService: LuceneReadService) : Command {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    override suspend fun process(): Mono<Message> = createEmbedStrip(luceneReadService.find(query))
}
