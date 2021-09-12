package fr.spacefox.calvinandbot.service

import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.repository.LuceneWriteRepository
import fr.spacefox.calvinandbot.repository.StripsRepository
import fr.spacefox.calvinandbot.util.LoggerDelegate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.Logger

class LuceneWriteService : KoinComponent {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    private val stripsRepository by inject<StripsRepository>()

    fun loadStrips() {
        log.info("Loading strips into Lucene")
        val strips = stripsRepository.validStrips()
        val repository = LuceneWriteRepository()
        repository.use {
            saveAll(repository, strips)
            flush(repository)
        }
        log.info("${strips.size} strips indexed in Lucene.")
    }

    private fun saveAll(repository: LuceneWriteRepository, strips: MutableList<Strip>) =
        strips.forEach { repository.save(it) }

    private fun flush(repository: LuceneWriteRepository) = repository.flush()
}
