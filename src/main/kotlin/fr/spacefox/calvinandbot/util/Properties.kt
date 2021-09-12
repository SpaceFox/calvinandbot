package fr.spacefox.calvinandbot.util

object Properties {
    private val prop = System.getProperties()

    fun value(name: String): String {
        return prop.getProperty(name)
    }

    fun value(name: String, defaultValue: String): String {
        return prop.getProperty(name, defaultValue)
    }
}
