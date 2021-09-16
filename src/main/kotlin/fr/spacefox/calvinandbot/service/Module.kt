package fr.spacefox.calvinandbot.service

import org.koin.dsl.module

val servicesModule = module {
    single { DiscordService() }
    single { LuceneReadService() }
    single { LuceneWriteService() }
    single { ScraperService() }
}
