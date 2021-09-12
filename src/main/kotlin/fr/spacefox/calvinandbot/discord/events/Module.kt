package fr.spacefox.calvinandbot.discord.events

import org.koin.dsl.module

val eventsModule = module {
    single { MessageListener() }
    single { MessageCreateListener(get()) }
    single { MessageUpdateListener(get()) }
}
