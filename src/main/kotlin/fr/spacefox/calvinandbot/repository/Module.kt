package fr.spacefox.calvinandbot.repository

import org.koin.dsl.module

val repositoryModule = module {
    single { LuceneReadRepository() }
    single { LuceneWriteRepository() }
    single { StripsRepository() }
}
