package fr.spacefox.calvinandbot.discord.commands

import discord4j.core.`object`.entity.Message
import fr.spacefox.calvinandbot.util.LoggerDelegate
import org.slf4j.Logger
import reactor.core.publisher.Mono

class HelpCommand(override val message: Message, val command: String) : Command {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    override suspend fun process(): Mono<Message> = createMessage {
        it.setMessageReference(message.id)
            .setContent(
                "You can call this bot with mention, `$command` and direct message.\n" +
                    "Commands:\n" +
                    "• `help`: You’re reading it!\n" +
                    "• `random`: gives you a random strip.\n" +
                    "• `yyyy-mm-dd`, `yy-mm-dd`, `dd/mm/yyyy` or `dd/mm/yy`: displays the strip for the given date " +
                    "_(between 18th november 1985 and 31th december 1995)_\n" +
                    "• `any other terms` will search a strip with provided terms in transcript.\n" +
                    "Search allows wildcards (single-character `te?t` or multiple characters `te*t`), " +
                    "regular expressions (`/[mb]oat/`), fuzzy search (`roam~`) and any Lucene-supported terms " +
                    "modifier as specified here: " +
                    "<https://lucene.apache.org/core/8_9_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#Term_Modifiers>"
            )
    }
}
