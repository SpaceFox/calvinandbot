package fr.spacefox.calvinandbot.discord.commands

import discord4j.core.`object`.entity.Message
import fr.spacefox.calvinandbot.util.LoggerDelegate
import org.slf4j.Logger
import reactor.core.publisher.Mono

class DidntUnderstandCommand(override val message: Message) : Command {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    override suspend fun process(): Mono<Message> = createMessage {
        it.setMessageReference(message.id)
            .setContent(
                "I didn’t understand. Type `help`, " +
                    "a date (formats `yyyy-mm-dd`, `yy-mm-dd`, `dd/mm/yyyy` or `dd/mm/yy`), " +
                    "something to search, " +
                    "or `help` for details."
            )
    }
}
