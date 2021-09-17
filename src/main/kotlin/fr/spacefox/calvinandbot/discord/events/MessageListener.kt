package fr.spacefox.calvinandbot.discord.events

import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.channel.Channel
import fr.spacefox.calvinandbot.discord.commands.Command
import fr.spacefox.calvinandbot.discord.commands.DateCommand
import fr.spacefox.calvinandbot.discord.commands.DidntUnderstandCommand
import fr.spacefox.calvinandbot.discord.commands.HelpCommand
import fr.spacefox.calvinandbot.discord.commands.NothingFoundCommand
import fr.spacefox.calvinandbot.discord.commands.RandomCommand
import fr.spacefox.calvinandbot.discord.commands.SearchCommand
import fr.spacefox.calvinandbot.service.LuceneReadService
import fr.spacefox.calvinandbot.util.LoggerDelegate
import fr.spacefox.calvinandbot.util.Properties
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MessageListener : KoinComponent {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    internal val command: String = Properties.value(Properties.BOT_COMMAND, "/calvin")

    private val isoDateRegex = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()
    private val isoShortDateRegex = "^\\d{2}-\\d{2}-\\d{2}$".toRegex()
    private val euDateRegex = "^\\d{2}/\\d{2}/\\d{4}$".toRegex()
    private val euShortDateRegex = "^\\d{2}/\\d{2}/\\d{2}$".toRegex()
    private val isoShortDateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd")
    private val euDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val euShortDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")

    private val luceneService by inject<LuceneReadService>()

    fun listen(messages: Flow<Message>): Flow<Message> = messages
        .buffer()
        .filter { notABot(it) }
        .map { toCommand(it) }
        .filterNotNull()
        .map { command ->
            command.process()
                .switchIfEmpty(NothingFoundCommand(command.message).process())
                .awaitSingle()
        }

    internal fun notABot(message: Message) = message.author.map { !it.isBot }.orElse(false)

    private fun directMessage(message: Message) = message.channel
        .map { it.type == Channel.Type.DM }
        .block()
        .let { it ?: false }

    private fun mention(message: Message) = message.userMentionIds.contains(message.client.selfId)

    private fun slashCommand(message: Message): Boolean {
        return message.content.startsWith(command, true)
    }

    internal suspend fun toCommand(message: Message): Command? {
        return when {
            directMessage(message) -> commandParser(message, message.content)
            mention(message) -> commandParser(message, removeMention(message.content))
            slashCommand(message) -> commandParser(message, removeSlashCommand(message.content))
            else -> null
        }
    }

    internal fun commandParser(message: Message, content: String): Command {
        val c = content.trim()
        return when {
            c.equals("help", true) -> HelpCommand(message, command)
            c.equals("random", true) -> RandomCommand(message, luceneService)
            c.matches(isoDateRegex) -> DateCommand(message, LocalDate.parse(c), luceneService)
            c.matches(euDateRegex) -> DateCommand(message, LocalDate.parse(c, euDateFormatter), luceneService)
            c.matches(isoShortDateRegex) -> DateCommand(
                message,
                LocalDate.parse(c, isoShortDateFormatter),
                luceneService
            )
            c.matches(euShortDateRegex) -> DateCommand(message, LocalDate.parse(c, euShortDateFormatter), luceneService)
            c.isNotBlank() -> SearchCommand(message, c, luceneService)
            else -> DidntUnderstandCommand(message)
        }
    }

    internal fun removeSlashCommand(content: String): String = content.replaceFirst(command, "")

    internal fun removeMention(content: String): String = content.replace("<(@|@!|@&|#)\\d+>".toRegex(), "")
}
