package fr.spacefox.calvinandbot.model

import org.apache.commons.text.StringEscapeUtils
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

object StripBodyToModelMapper {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun stripBodyToModel(body: String): Strip {
        val comicDiv = Jsoup
            .parse(body)
            .select("div.comic")[0]

        return Strip(
            id = comicDiv.attr("data-shareable-id").toInt(),
            author = comicDiv.attr("data-creator"),
            image = comicDiv.attr("data-image"),
            url = comicDiv.attr("data-url"),
            title = comicDiv.attr("data-title"),
            transcript = cleanTranscript(comicDiv.attr("data-transcript")),
            rawTranscript = comicDiv.attr("data-transcript"),
            publishDate = LocalDate.parse(comicDiv.attr("data-date"), dateFormatter),
            tags = toTags(comicDiv.attr("data-tags"))
        )
    }

    internal fun cleanTranscript(transcript: String): String {
        return """ ?([A-Z]\w+:) """.toRegex()
            .replace(StringEscapeUtils.unescapeHtml4(transcript.trim())) { "\n— ${it.groups[1]?.value} " }
            .trim()
    }

    internal fun toTags(s: String): Set<String> {
        return s.split(",").stream()
            .map { it.trim() }
            .map { StringEscapeUtils.unescapeHtml4(it) }
            .collect(Collectors.toSet())
    }
}
