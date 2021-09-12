package fr.spacefox.calvinandbot.repository

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.util.Properties
import java.net.URL

class StripsRepository {

    private var dataPath = Properties.value(
        "strips.dataPath",
        "file:/home/spacefox/dev/kotlin/calvinandbot/src/main/resources/comics/calvin-and-hobbes.json"
    )

    fun stripsFromJson(): Array<Strip?> {
        val mapper = jacksonObjectMapper().findAndRegisterModules()
//        val src = BotConfiguration::class.java.getResource("/comics/calvin-and-hobbes.json")
//        return mapper.readValue(src)
        return mapper.readValue(URL(dataPath))
    }

    fun validStrips() = stripsFromJson()
        .filterNotNull()
        // Strips without transcripts are filling strips on date without original publication
        .filter { it.rawTranscript.isNotEmpty() }
        .toMutableList()
}
