package fr.spacefox.calvinandbot.model

import java.time.LocalDate

data class Strip(
    val id: Int,
    val author: String,
    val image: String,
    val url: String,
    val title: String,
    val transcript: String,
    val rawTranscript: String,
    val publishDate: LocalDate,
    val tags: Set<String>
) {
    companion object {
        val emptyStrip = Strip(
            id = 0,
            author = "",
            image = "",
            url = "",
            title = "",
            transcript = "",
            rawTranscript = "",
            publishDate = LocalDate.MIN,
            tags = setOf()
        )
    }
}
