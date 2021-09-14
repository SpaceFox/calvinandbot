package fr.spacefox.calvinandbot.discord.commands

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import fr.spacefox.calvinandbot.model.Strip
import io.mockk.coVerify
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.time.LocalDate
import kotlin.test.assertEquals

internal class CommandTest : CommandTestCommon() {

    private val strip = Strip(
        id = 1,
        author = "Author",
        image = "https://path.to/image.jpg",
        url = "https://path.to/strip",
        title = "Title",
        transcript = "Transcript",
        rawTranscript = "Raw Transcript",
        publishDate = LocalDate.of(1990, 1, 2),
        tags = setOf("Tag 1", "Tag 2")
    )

    private val command = spyk(object : Command {
        override val message: Message
            get() = mockMessage

        override suspend fun process(): Mono<Message> {
            return Mono.empty()
        }
    })

    @Test
    fun `createEmbedStrip with empty strip`() = runBlockingTest {
        // When
        val actual = command.createEmbedStrip(Strip.emptyStrip)

        // Then
        assertEquals(Mono.empty(), actual)
        coVerify(exactly = 0) { mockMessage.channel }
    }

    @Test
    fun stripToMessageSpec() {
        // Given
        val mockSpec: MessageCreateSpec = spyk(MessageCreateSpec())
        val id = Snowflake.of(1)
        every { mockMessage.id } returns id

        // When
        command.stripToMessageSpec(mockSpec, strip)

        // Then
        verify {
            command.stripToEmbedSpec(any(), eq(strip))
            mockSpec.setMessageReference(id)
            mockSpec.addEmbed(any())
        }
    }

    @Test
    fun stripToEmbedSpec() {
        // Given
        val mockSpec = spyk(EmbedCreateSpec())

        // When
        command.stripToEmbedSpec(mockSpec, strip)

        // Then
        verify {
            mockSpec.setAuthor("Author", null, null)
            mockSpec.setImage("https://path.to/image.jpg")
            mockSpec.setUrl("https://path.to/strip")
            mockSpec.setTitle("Title")
            mockSpec.setDescription("Transcript")
            mockSpec.setFooter("Tags: Tag 1, Tag 2", null)
        }
    }
}
