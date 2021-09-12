package fr.spacefox.calvinandbot.repository

import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.model.StripDocumentModel
import fr.spacefox.calvinandbot.model.StripToLuceneDocMapper
import fr.spacefox.calvinandbot.util.LoggerDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.lucene.document.LongPoint
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.Sort
import org.apache.lucene.search.SortField
import org.apache.lucene.search.SortedNumericSortField
import org.apache.lucene.store.Directory
import org.slf4j.Logger
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

class LuceneReadRepository : AutoCloseable {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    private val repository = LuceneRepository()
    private val reader: DirectoryReader = DirectoryReader.open(repository.directory)
    private val searcher: IndexSearcher = IndexSearcher(reader)

    override fun close() {
        reader.close()
        repository.close()
    }

    suspend fun findByPublishDate(date: LocalDate): Strip {
        return search(LongPoint.newExactQuery(StripDocumentModel.publishDateSearch, date.toEpochDay()))
    }

    suspend fun find(queryString: String): Strip {
        val query = MultiFieldQueryParser.parse(
            arrayOf(queryString, queryString),
            arrayOf(StripDocumentModel.tag, StripDocumentModel.rawTranscript),
            repository.analyzer
        )
        return search(query)
    }

    internal suspend fun search(query: Query): Strip = search(query, null)

    internal suspend fun search(query: Query, sort: Sort?): Strip {
        val hits = withContext(Dispatchers.IO) {
            when (sort) {
                null -> searcher.search(query, 1)
                else -> searcher.search(query, 1, sort)
            }.scoreDocs
        }
        return if (hits.isEmpty()) Strip.emptyStrip else toDoc(hits[0])
    }

    private fun toDoc(scoreDoc: ScoreDoc): Strip {
        return StripToLuceneDocMapper.toStrip(searcher.doc(scoreDoc.doc))
    }

    @Deprecated("Mono, à supprimer")
    fun findLast(): Mono<Strip> {
        val query = Mono.just(MatchAllDocsQuery())
        val sort = Mono.just(Sort(SortedNumericSortField("publishDate", SortField.Type.LONG, true)))
        val searcher = luceneSearcher(repository.directory)
        return Mono.zip(searcher, query, sort)
            .map { it.t1.search(it.t2, 1, it.t3) }
            .flatMapMany { Flux.fromArray(it.scoreDocs) }
            .next()
            .zipWith(searcher)
            .map { it.t2.doc(it.t1.doc) }
            .map { StripToLuceneDocMapper.toStrip(it) }
    }

    @Deprecated("Mono, à supprimer")
    fun luceneSearcher(directory: Directory): Mono<IndexSearcher> {
        val reader = Mono.just(DirectoryReader.open(directory))
//        reader.use {
        return reader.map { IndexSearcher(it) }
//        }
    }
}
