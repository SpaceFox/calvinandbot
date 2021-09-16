package fr.spacefox.calvinandbot.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.util.Properties
import java.io.File
import java.net.URI
import java.time.LocalDate

class StripsRepository {

    val mapper = jacksonObjectMapper().findAndRegisterModules()
    val dataFile = File(
        URI(
            Properties.value(
                "strips.dataPath",
                "file:/home/spacefox/dev/kotlin/calvinandbot-lite/src/main/resources/comics/calvin-and-hobbes.json"
            )
        )
    )
    var strips: MutableList<Strip?> = mutableListOf()

    fun stripsFromJson(): MutableList<Strip?> {
        return mapper.readValue(dataFile)
    }

    fun validStrips(): MutableList<Strip> {
        if (strips.isEmpty()) {
            strips = stripsFromJson()
        }
        return strips
            .filterNotNull()
            // Strips without transcripts are filling strips on date without original publication
            .filter { it.rawTranscript.isNotEmpty() }
            .toMutableList()
    }

    // Dont’t use valid strips only to avoid re-scraping of invalid strips
    fun lastUpdate(defaultDate: LocalDate): LocalDate = stripsFromJson()
        .filterNotNull()
        .map { it.publishDate }
        .maxOrNull()
        ?: defaultDate

    fun add(strip: Strip) {
        strips.add(strip)
    }

    fun save() {
        return mapper
            .writerWithDefaultPrettyPrinter()
            .writeValue(dataFile, strips)
    }
}
