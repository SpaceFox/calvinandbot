package fr.spacefox.calvinandbot.discord.commands

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

internal class DateCommandTest : CommandTestCommon() {

    @Test
    fun process() = runBlockingTest {
        // Given
        val command = spyk(DateCommand(mockMessage, mockDate, mockLuceneService))
        coEvery { command.createEmbedStrip(any()) } returns Mono.empty()
        coEvery { mockLuceneService.findByPublishDate(mockDate) } returns mockStrip

        // When
        command.process()

        // Then
        coVerify {
            mockLuceneService.findByPublishDate(mockDate)
            command.createEmbedStrip(any())
        }
    }
}
