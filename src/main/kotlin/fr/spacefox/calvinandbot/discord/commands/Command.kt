package fr.spacefox.calvinandbot.discord.commands

import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.util.LoggerDelegate
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.Logger
import reactor.core.publisher.Mono
import java.util.function.Consumer
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
        else createMessage { stripToMessageSpec(it, strip) }
    }

    suspend fun createMessage(spec: Consumer<in MessageCreateSpec>): Mono<Message> = message
        .channel
        .awaitSingle()
        .createMessage(spec)

    fun stripToMessageSpec(it: MessageCreateSpec, strip: Strip) {
        it.setMessageReference(message.id)
            .addEmbed { stripToEmbedSpec(it, strip) }
    }

    fun stripToEmbedSpec(spec: EmbedCreateSpec, strip: Strip) {
        val sortedTags = strip.tags.stream().sorted().collect(Collectors.joining(", "))
        spec.setAuthor(strip.author, null, null)
            .setImage(strip.image)
            .setUrl(strip.url)
            .setTitle(strip.title)
            .setDescription(strip.transcript)
            .setFooter("Tags: $sortedTags", null)
    }
}
