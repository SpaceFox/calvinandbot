package fr.spacefox.calvinandbot.discord.commands

import discord4j.core.`object`.entity.Message
import fr.spacefox.calvinandbot.util.LoggerDelegate
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import reactor.core.publisher.Mono

const val EMOJI_NO = "\uD83D\uDEAB"
const val EMOJI_BOY = "\uD83D\uDC66"
const val EMOJI_TIGER = "\uD83D\uDC05"

class NothingFoundCommand(override val message: Message) : Command {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    override suspend fun process(): Mono<Message> {
        return message.channel.awaitSingle().createMessage { spec ->

            spec.setMessageReference(message.id)
                .setContent("Sorry, strip found :no_entry_sign: :boy: :tiger:")
        }
    }
}
