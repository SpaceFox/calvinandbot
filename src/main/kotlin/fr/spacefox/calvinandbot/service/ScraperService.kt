package fr.spacefox.calvinandbot.service

import fr.spacefox.calvinandbot.model.StripBodyToModelMapper
import fr.spacefox.calvinandbot.repository.StripsRepository
import fr.spacefox.calvinandbot.util.LoggerDelegate
import fr.spacefox.calvinandbot.util.Properties
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.features.BrowserUserAgent
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.stream.Stream

class ScraperService : KoinComponent {
    companion object {
        val log: Logger by LoggerDelegate()
        val firstPublish: LocalDate = LocalDate.of(1985, 11, 18)
        val lastPublish: LocalDate = LocalDate.of(1995, 12, 31)
    }

    private val stripsRepository by inject<StripsRepository>()

    private val enabled = Properties.value(Properties.SCRAPER_ENABLED, false)
    private val delayMs = Properties.value(Properties.SCRAPER_DELAY_MS, 1000)
    private val timeoutMs = Properties.value(Properties.SCRAPER_TIMEOUT_MS, 30_000)

    private val dateToUrlPartFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    private val httpClient: HttpClient = HttpClient(CIO) {
        if (log.isDebugEnabled) {
            install(Logging) { level = LogLevel.HEADERS }
        }
        BrowserUserAgent()
        engine {
            endpoint {
                connectTimeout = timeoutMs
            }
        }
    }

    suspend fun init() {
        log.info("Initializing Scraper Service")

        log.info("Scraper service is ${if (enabled) "enabled" else "disabled"}")

        if (enabled) {
            if (stripsRepository.useInternalData) {
                log.error("Scraper service requires an external data path to store scraped data. " +
                    "Please set it with -D${Properties.STRIPS_DATA_URL} parameter. No scraping will be done.")
            } else {
                log.info(" -> Scraper will get a new strip each $delayMs ms with a $timeoutMs ms timeout.")
                scrape()
            }
        }
    }

    suspend fun scrape() {
        val lastKnowStrip = stripsRepository.lastUpdate(firstPublish)
        log.info("Last strip found is strip of $lastKnowStrip")

        Stream.iterate(lastKnowStrip) { d: LocalDate -> d.plusDays(1) }
            .limit(ChronoUnit.DAYS.between(lastKnowStrip, lastPublish) + 1)

        dateFlow(lastKnowStrip)
            .onEach { delay(delayMs) }
            .map { it.format(dateToUrlPartFormatter) }
            .map {
                val url = "https://www.gocomics.com/calvinandhobbes/$it"
                log.info("Downloading strip from $url")
                withContext(Dispatchers.IO) {
                    httpClient
                        .get<HttpStatement>(url)
                        .receive<String>()
                }
            }
            .map { StripBodyToModelMapper.stripBodyToModel(it) }
            .map { stripsRepository.add(it) }
            .collect { withContext(Dispatchers.IO) { stripsRepository.save() } }
        log.warn("Scraping finished! Restart server to index content.")
    }

    private fun dateFlow(lastKnowStrip: LocalDate): Flow<LocalDate> = flow {
        var date = lastKnowStrip
        while (date.isBefore(lastPublish) || date.isEqual(lastPublish)) {
            emit(date)
            date = date.plusDays(1)
        }
    }
}
