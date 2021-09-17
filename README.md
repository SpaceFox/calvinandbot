# Calvin and Bot

A very simple Discord bot that displays contents from Calvin and Hobbes comics.

This shares comics from [GoComics](https://www.gocomics.com/calvinandhobbes) and will look only on original publication
dates (between 18th november 1985 and 31th december 1995).

# Inviting the bot to your Discord server

[Click here to invite the bot to your Discord server](https://discord.com/oauth2/authorize?client_id=880130635254075482&scope=bot&permissions=19456)

# How to interact with it

You can call this bot with mention, `/calvin` (customizable) and direct message.

Commands:
- `help`: You’re reading it!
- `random`: gives you a random strip.
- `yyyy-mm-dd`, `yy-mm-dd`, `dd/mm/yyyy` or `dd/mm/yy`: displays the strip for the given date _(between 18th november 
  1985 and 31th december 1995)_ (sorry to US people, you’ll have to use logical date formats).
- `any other terms` will search a strip with provided terms in transcript.

Search allows wilcards (single-character `te?t` or multiple characters `te*t`), regular expressions (`/[mb]oat/`), fuzzy
search (`roam~`) and any Lucene-supported terms modifier [as specified here](https://lucene.apache.org/core/8_9_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html#Term_Modifiers).

# How to run it

## Build

There is a `shadowWar` Gradle command to build a single executable Jar file : `./gradlew shadowJar`

## Run

Just run it as a standard executable Jar:

```
java -Xmx64m -Ddiscord4j.token={your token here} -jar calvinandbot-1.0.0-all.jar
```

## Configure

This bot will accept following parameters as Java VM options (`-Dparameter.name=value` form):

Name | Type | Default | Comment
-----|------|---------|--------
`discord4j.token` | String | _None_ | **Mandatory**. Your [Discord bot token](https://discord.com/developers/applications).
`bot.command` | String | `/calvin` | The command to trigger the bot in public channels.
`lucene.index.path` | Path (as String) | `lucene/` | Path to store Lucene indexation files.
`strips.dataUrl` | URL (as String) | _Empty_ | URL to store the strips data to use, before indexation. If empty, blank or non-provided, the bot will use embedded data (should be complete).
`scraper.enabled` | Boolean (`true` or `false`) | `false` | Enables the data scraper to re-download strips data from GoComics.
`scraper.delayMs` | Integer | 1000 | Delay between two strips loads, in milliseconds. Used only if scraper is enabled.
`scraper.timeoutMs` | Integer | 30000 | Timeout for strip scraping, in milliseconds. Used only if scraper is enabled.

# Real project’s goal

OK, this is mainly a test for various technologies, as [Kotlin coroutines](https://kotlinlang.org/docs/coroutines-guide.html),
[Gradle’s Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html), [Discord4J](https://discord4j.com), 
embedded [Lucene index](https://lucene.apache.org) (there was a private ElasticSearch + Spring Boot version of this bot,
which required way more RAM), [Koin](https://insert-koin.io) (dependency injection) [Ktor](https://ktor.io) (HTTP client),
[JSoup](https://jsoup.org) (HTML parsing) and [MockK](https://mockk.io) (mocking).

This also explains the lack of CI, very partial test coverage (some tests are quite complicated) and deployment stuff.

Transcripts from GoComics contains lots of typos, and indexation settings are badly tuned, thus this bot will probably 
give quite bad results.

