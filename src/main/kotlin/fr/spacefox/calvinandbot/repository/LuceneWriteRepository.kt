package fr.spacefox.calvinandbot.repository

import fr.spacefox.calvinandbot.model.Strip
import fr.spacefox.calvinandbot.model.StripToLuceneDocMapper
import fr.spacefox.calvinandbot.util.LoggerDelegate
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.slf4j.Logger

class LuceneWriteRepository : AutoCloseable {
    companion object {
        val log: Logger by LoggerDelegate()
    }

    private val repository = LuceneRepository()
    private val config: IndexWriterConfig = IndexWriterConfig(repository.analyzer)
    private val writer: IndexWriter = IndexWriter(repository.directory, config)

    override fun close() {
        writer.close()
        repository.close()
    }

    fun save(strip: Strip): Strip {
        val doc = StripToLuceneDocMapper.toDoc(strip)
        if (log.isDebugEnabled) {
            log.debug("Save to Lucene : $doc")
        }
        writer.addDocument(doc)
        return strip
    }

    fun commit() {
        writer.flush()
        writer.commit()
    }

    fun clearAll() {
        writer.deleteAll()
        commit()
        writer.deleteUnusedFiles()
    }
}
