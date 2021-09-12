package fr.spacefox.calvinandbot.application

import fr.spacefox.calvinandbot.discord.events.eventsModule
import fr.spacefox.calvinandbot.repository.repositoryModule
import fr.spacefox.calvinandbot.service.DiscordService
import fr.spacefox.calvinandbot.service.LuceneReadService
import fr.spacefox.calvinandbot.service.LuceneWriteService
import fr.spacefox.calvinandbot.service.servicesModule
import fr.spacefox.calvinandbot.util.LoggerDelegate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.slf4j.Logger

fun main(args: Array<String>) {

    startKoin {
        modules(servicesModule, repositoryModule, eventsModule)
    }
    CalvinandbotApplication().launch()
}

class CalvinandbotApplication : KoinComponent {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    private val discordService by inject<DiscordService>()
    private val luceneService by inject<LuceneReadService>()
    private val luceneWriteService by inject<LuceneWriteService>()

    fun launch() {
        log.info("Launching Calvin and Bot")

        // Synchronous loading data into Lucene Repository
        // Required because Lucene index cannot be update while reading, thus it must be fully loaded before reading.
        luceneWriteService.loadStrips()

        luceneService.init()
        discordService.init()
    }
}
