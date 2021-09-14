package fr.spacefox.calvinandbot.discord.commands

import discord4j.core.`object`.entity.Message
import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.service.LuceneReadService
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate

internal abstract class CommandTestCommon {

    val mockStrip = mockk<Strip>()

    val mockMessage = mockk<Message>()

    val mockDate = mockk<LocalDate>()

    val mockLuceneService = mockk<LuceneReadService>()

    val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}
