package fr.spacefox.calvinandbot.util

object Properties {
    private val prop = System.getProperties()

    fun value(name: String): String {
        return prop.getProperty(name)
    }

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
