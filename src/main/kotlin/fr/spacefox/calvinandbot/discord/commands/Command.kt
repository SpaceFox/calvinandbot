package fr.spacefox.calvinandbot.discord.commands

import discord4j.core.`object`.entity.Message
import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.util.LoggerDelegate
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import reactor.core.publisher.Mono
import java.util.stream.Collectors

interface Command {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    val message: Message

    suspend fun process(): Mono<Message>

    suspend fun createEmbedStrip(strip: Strip): Mono<Message> {
        return if (strip == Strip.emptyStrip)
            Mono.empty()
        else message.channel.awaitSingle()
            .createMessage {
                it.setMessageReference(message.id)
                    .addEmbed { spec ->
                        val sortedTags = strip.tags.stream().sorted().collect(Collectors.joining(", "))
                        spec.setAuthor(strip.author, null, null)
                            .setImage(strip.image)
                            .setUrl(strip.url)
                            .setTitle(strip.title)
                            .setDescription(strip.transcript)
                            .setFooter("Tags: $sortedTags", null)
                    }
            }
    }
}
