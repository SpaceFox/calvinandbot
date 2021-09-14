package fr.spacefox.calvinandbot.discord.commands

import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class DidntUnderstandCommandTest : CommandTestCommon() {

    @Test
    @Disabled
    fun process() = runBlockingTest {
        // Given
        val commmand = spyk(DidntUnderstandCommand(mockMessage))

        // When
        commmand.process()

        // Then
        coVerify {
            commmand.createMessage(any())
        }
    }
}
