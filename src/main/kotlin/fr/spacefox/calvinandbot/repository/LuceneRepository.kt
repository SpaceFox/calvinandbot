package fr.spacefox.calvinandbot.repository

import fr.spacefox.calvinandbot.util.Properties
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.store.FSDirectory
import java.nio.file.Path

internal class LuceneRepository : AutoCloseable {

    private val pathName: String = Properties.value("lucene.index.path", "/home/spacefox/dev/kotlin/calvinandbot-lite/lucene")
    internal val directory: FSDirectory = FSDirectory.open(Path.of(pathName))
    internal val analyzer: StandardAnalyzer = StandardAnalyzer()

    override fun close() {
        analyzer.close()
        directory.close()
    }
}
