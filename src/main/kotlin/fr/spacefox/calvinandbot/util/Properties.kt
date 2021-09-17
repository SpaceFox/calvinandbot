package fr.spacefox.calvinandbot.util

object Properties {

    const val DISCORD4J_TOKEN = "discord4j.token"

    const val BOT_COMMAND = "bot.command"

    const val LUCENE_INDEX_PATH = "lucene.index.path"

    const val STRIPS_DATA_URL = "strips.dataUrl"

    const val SCRAPER_ENABLED = "scraper.enabled"
    const val SCRAPER_DELAY_MS = "scraper.delayMs"
    const val SCRAPER_TIMEOUT_MS = "scraper.timeoutMs"

    private val prop = System.getProperties()

    fun value(name: String, defaultValue: String): String {
        return prop.getProperty(name, defaultValue)
    }

    fun value(name: String, defaultValue: Boolean): Boolean {
        return if (prop.containsKey(name))
            (prop.getProperty(name).toBooleanStrictOrNull() ?: defaultValue)
        else
            defaultValue
    }

    fun value(name: String, defaultValue: Long): Long {
        return if (prop.containsKey(name))
            (prop.getProperty(name).toLongOrNull() ?: defaultValue)
        else
            defaultValue
    }
}
