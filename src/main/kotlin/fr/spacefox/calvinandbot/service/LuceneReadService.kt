package fr.spacefox.calvinandbot.service

import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.repository.LuceneReadRepository
import fr.spacefox.calvinandbot.repository.StripsRepository
import fr.spacefox.calvinandbot.util.LoggerDelegate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger
import java.time.Instant
import java.time.LocalDate
import java.util.stream.Collectors

class LuceneReadService : KoinComponent {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    private val stripsRepository by inject<StripsRepository>()
    private val repository by inject<LuceneReadRepository>()
    private var stripDates = emptyList<LocalDate>()

    var lastUpdate: Instant = Instant.MIN

    fun init() {
        log.info("Initializing Lucene Reading Service")
        stripDates = stripsRepository.validStrips()
            .stream()
            .map { it.publishDate }
            .collect(Collectors.toUnmodifiableList())
    }

    suspend fun findByPublishDate(date: LocalDate): Strip = repository.findByPublishDate(date)

    suspend fun find(query: String): Strip = repository.find(query)

    suspend fun findRandom(): Strip = repository.findByPublishDate(stripDates.random())
}
