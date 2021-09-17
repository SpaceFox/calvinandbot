package fr.spacefox.calvinandbot.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.util.LoggerDelegate
import fr.spacefox.calvinandbot.util.Properties
import org.slf4j.Logger
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.security.InvalidParameterException
import java.time.LocalDate

class StripsRepository {

    companion object {
        val log: Logger by LoggerDelegate()
    }

    private val mapper = jacksonObjectMapper().findAndRegisterModules()
    private val dataUrl: URL
    var strips: MutableList<Strip?> = mutableListOf()
    val useInternalData: Boolean

    init {
        val dataUrlParam = Properties.value(Properties.STRIPS_DATA_URL, "")
        val maybeDataFile: URL? = if (dataUrlParam.isNotBlank()) {
            useInternalData = false
            dataUrlParam.toURL()
        } else {
            useInternalData = true
            StripsRepository::class.java.getResource("/comics/calvin-and-hobbes.json")
        }
        if (maybeDataFile == null) {
            throw InvalidParameterException("Invalid value read from ${Properties.STRIPS_DATA_URL}, " +
                "expected a valid URL but found \"$dataUrlParam\".")
        }
        dataUrl = maybeDataFile
    }

    fun stripsFromJson(): MutableList<Strip?> {
        return mapper.readValue(dataUrl)
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
            .writeValue(File(dataUrl.toURI()), strips)
    }
}

private fun String.toURL(): URL? {
    return try {
        if (this.isBlank()) null else URL(this)
    } catch (e: MalformedURLException) {
        null
    }
}
